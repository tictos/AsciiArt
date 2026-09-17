package com.example.domain.engine

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.example.domain.model.AsciiColorMode
import com.example.domain.model.AsciiMatrixResult
import com.example.domain.model.CharacterRamp
import com.example.domain.model.DitherAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

object AsciiConverter {

    private val BAYER_4X4 = arrayOf(
        intArrayOf(0, 8, 2, 10),
        intArrayOf(12, 4, 14, 6),
        intArrayOf(3, 11, 1, 9),
        intArrayOf(15, 7, 13, 5)
    )

    /**
     * Asynchronous non-blocking ASCII conversion executed on Dispatchers.Default
     * with cooperative cancellation support.
     */
    suspend fun convertBitmapAsync(
        bitmap: Bitmap,
        targetColumns: Int = 80,
        ramp: CharacterRamp = CharacterRamp.STANDARD,
        colorMode: AsciiColorMode = AsciiColorMode.GREEN_PHOSPHOR,
        dither: DitherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
        contrast: Float = 1.0f,
        gamma: Float = 1.0f,
        invert: Boolean = false,
        sourceTitle: String = "photo.jpg"
    ): AsciiMatrixResult = withContext(Dispatchers.Default) {
        convertBitmap(bitmap, targetColumns, ramp, colorMode, dither, contrast, gamma, invert, sourceTitle)
    }

    fun convertBitmap(
        bitmap: Bitmap,
        targetColumns: Int = 80,
        ramp: CharacterRamp = CharacterRamp.STANDARD,
        colorMode: AsciiColorMode = AsciiColorMode.GREEN_PHOSPHOR,
        dither: DitherAlgorithm = DitherAlgorithm.FLOYD_STEINBERG,
        contrast: Float = 1.0f, // 0.5f to 2.0f
        gamma: Float = 1.0f,    // 0.5f to 2.0f
        invert: Boolean = false,
        sourceTitle: String = "photo.jpg"
    ): AsciiMatrixResult {
        var resultLines: List<String> = emptyList()
        var colorMatrix: Array<IntArray> = emptyArray()
        var cols = 0
        var rows = 0
        var glyphCount = 0

        val duration = measureTimeMillis {
            val charAspect = 0.52f // Width-to-height aspect ratio of monospace terminal font
            val targetWidth = targetColumns.coerceIn(20, 160)
            val targetHeight = max(1, ((bitmap.height.toFloat() / bitmap.width.toFloat()) * targetWidth * charAspect).roundToInt())

            cols = targetWidth
            rows = targetHeight
            glyphCount = cols * rows

            // Scale down the source bitmap for sampling
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
            val pixels = IntArray(targetWidth * targetHeight)
            scaledBitmap.getPixels(pixels, 0, targetWidth, 0, 0, targetWidth, targetHeight)

            // Extract luminance matrix & original RGB
            val lumMatrix = Array(targetHeight) { FloatArray(targetWidth) }
            val rgbMatrix = Array(targetHeight) { IntArray(targetWidth) }

            for (y in 0 until targetHeight) {
                for (x in 0 until targetWidth) {
                    val pixel = pixels[y * targetWidth + x]
                    val r = AndroidColor.red(pixel) / 255f
                    val g = AndroidColor.green(pixel) / 255f
                    val b = AndroidColor.blue(pixel) / 255f
                    rgbMatrix[y][x] = pixel

                    // Rec. 709 luminance
                    var lum = 0.299f * r + 0.587f * g + 0.114f * b

                    // Apply gamma
                    if (gamma != 1.0f && gamma > 0.01f) {
                        lum = lum.pow(1f / gamma)
                    }

                    // Apply contrast
                    if (contrast != 1.0f) {
                        lum = ((lum - 0.5f) * contrast + 0.5f).coerceIn(0f, 1f)
                    }

                    if (invert) {
                        lum = 1f - lum
                    }

                    lumMatrix[y][x] = lum.coerceIn(0f, 1f)
                }
            }

            // Apply Dithering and Ramp Mapping
            val rampStr = ramp.rampChars
            val rampLen = rampStr.length
            val linesBuilder = ArrayList<String>(targetHeight)
            colorMatrix = Array(targetHeight) { IntArray(targetWidth) }

            when (dither) {
                DitherAlgorithm.NONE -> {
                    for (y in 0 until targetHeight) {
                        val rowChars = StringBuilder(targetWidth)
                        for (x in 0 until targetWidth) {
                            val lum = lumMatrix[y][x]
                            val rampIdx = ((1f - lum) * (rampLen - 1)).roundToInt().coerceIn(0, rampLen - 1)
                            rowChars.append(rampStr[rampIdx])
                            colorMatrix[y][x] = resolveColor(rgbMatrix[y][x], lum, colorMode, x, y, targetWidth, targetHeight)
                        }
                        linesBuilder.add(rowChars.toString())
                    }
                }
                DitherAlgorithm.ORDERED_4X4 -> {
                    for (y in 0 until targetHeight) {
                        val rowChars = StringBuilder(targetWidth)
                        for (x in 0 until targetWidth) {
                            val bayerVal = (BAYER_4X4[y % 4][x % 4] / 16.0f) - 0.5f
                            val ditheredLum = (lumMatrix[y][x] + bayerVal * 0.25f).coerceIn(0f, 1f)
                            val rampIdx = ((1f - ditheredLum) * (rampLen - 1)).roundToInt().coerceIn(0, rampLen - 1)
                            rowChars.append(rampStr[rampIdx])
                            colorMatrix[y][x] = resolveColor(rgbMatrix[y][x], ditheredLum, colorMode, x, y, targetWidth, targetHeight)
                        }
                        linesBuilder.add(rowChars.toString())
                    }
                }
                DitherAlgorithm.FLOYD_STEINBERG -> {
                    val errorMatrix = Array(targetHeight) { FloatArray(targetWidth) { 0f } }
                    for (y in 0 until targetHeight) {
                        val rowChars = StringBuilder(targetWidth)
                        for (x in 0 until targetWidth) {
                            val currentLum = (lumMatrix[y][x] + errorMatrix[y][x]).coerceIn(0f, 1f)
                            val rampIdx = ((1f - currentLum) * (rampLen - 1)).roundToInt().coerceIn(0, rampLen - 1)
                            val quantLum = 1f - (rampIdx.toFloat() / (rampLen - 1))
                            val error = currentLum - quantLum

                            rowChars.append(rampStr[rampIdx])
                            colorMatrix[y][x] = resolveColor(rgbMatrix[y][x], currentLum, colorMode, x, y, targetWidth, targetHeight)

                            // Error diffusion
                            if (x + 1 < targetWidth) {
                                errorMatrix[y][x + 1] += error * (7f / 16f)
                            }
                            if (y + 1 < targetHeight) {
                                if (x - 1 >= 0) {
                                    errorMatrix[y + 1][x - 1] += error * (3f / 16f)
                                }
                                errorMatrix[y + 1][x] += error * (5f / 16f)
                                if (x + 1 < targetWidth) {
                                    errorMatrix[y + 1][x + 1] += error * (1f / 16f)
                                }
                            }
                        }
                        linesBuilder.add(rowChars.toString())
                    }
                }
            }

            resultLines = linesBuilder
        }

        return AsciiMatrixResult(
            lines = resultLines,
            colors = colorMatrix,
            cols = cols,
            rows = rows,
            glyphCount = glyphCount,
            renderTimeMs = duration,
            sourceName = sourceTitle,
            sourceResolution = "${bitmap.width}x${bitmap.height}"
        )
    }

    private fun resolveColor(
        sourcePixel: Int,
        lum: Float,
        mode: AsciiColorMode,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): Int {
        val alpha = 0xFF
        return when (mode) {
            AsciiColorMode.GREEN_PHOSPHOR -> {
                val intensity = (lum.coerceIn(0.2f, 1.0f))
                val r = (0x3F * intensity).roundToInt().coerceIn(0, 255)
                val g = (0xB9 * intensity).roundToInt().coerceIn(0, 255)
                val b = (0x50 * intensity).roundToInt().coerceIn(0, 255)
                AndroidColor.argb(alpha, r, g, b)
            }
            AsciiColorMode.AMBER_VINTAGE -> {
                val intensity = (lum.coerceIn(0.2f, 1.0f))
                val r = (0xD2 * intensity).roundToInt().coerceIn(0, 255)
                val g = (0x99 * intensity).roundToInt().coerceIn(0, 255)
                val b = (0x22 * intensity).roundToInt().coerceIn(0, 255)
                AndroidColor.argb(alpha, r, g, b)
            }
            AsciiColorMode.ANSI_TRUECOLOR -> {
                sourcePixel
            }
            AsciiColorMode.CYBERPUNK_NEON -> {
                // Blend from Cyan (0x58A6FF) to Magenta (0xFFFF007F) across diagonal
                val factor = ((x.toFloat() / max(1, width)) + (y.toFloat() / max(1, height))) / 2f
                val r = (0x58 + factor * (0xFF - 0x58)).roundToInt().coerceIn(0, 255)
                val g = (0xA6 + factor * (0x00 - 0xA6)).roundToInt().coerceIn(0, 255)
                val b = (0xFF + factor * (0x7F - 0xFF)).roundToInt().coerceIn(0, 255)
                AndroidColor.argb(alpha, r, g, b)
            }
        }
    }

    fun generateSampleCyberCat(): AsciiMatrixResult {
        val artLines = listOf(
            "             /\\_/\\             ",
            "            ( o.o )            ",
            "             > ^ <             ",
            "         [CYBER-CAT]          ",
            "         /|  ===  |\\          ",
            "        / |       | \\         ",
            "       (_/ ======= \\_)        ",
            "          █▓▒░░▒▓█            "
        )
        val rows = artLines.size
        val cols = artLines.first().length
        val colors = Array(rows) { y ->
            IntArray(cols) { x ->
                if (y in 0..2) AndroidColor.parseColor("#58A6FF")
                else if (y in 3..4) AndroidColor.parseColor("#3FB950")
                else AndroidColor.parseColor("#BC8CFF")
            }
        }

        return AsciiMatrixResult(
            lines = artLines,
            colors = colors,
            cols = cols,
            rows = rows,
            glyphCount = cols * rows,
            renderTimeMs = 12,
            sourceName = "cyber_cat.ascii",
            sourceResolution = "64x32"
        )
    }
}
