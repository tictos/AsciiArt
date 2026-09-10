package com.example.domain.engine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.domain.model.AsciiMatrixResult
import com.example.domain.model.ExportBackgroundAmbiance
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object ImageExporter {

    fun createArtworkBitmap(
        matrix: AsciiMatrixResult,
        ambiance: ExportBackgroundAmbiance,
        includeWatermark: Boolean,
        scaleMultiplier: Int = 2
    ): Bitmap {
        val baseCharWidth = 14f * scaleMultiplier
        val baseCharHeight = 24f * scaleMultiplier
        val padding = 40f * scaleMultiplier

        val imageWidth = (matrix.cols * baseCharWidth + padding * 2).toInt()
        val imageHeight = (matrix.rows * baseCharHeight + padding * 2 + if (includeWatermark) 40f * scaleMultiplier else 0f).toInt()

        val bitmap = Bitmap.createBitmap(
            max(100, imageWidth),
            max(100, imageHeight),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // 1. Draw background
        when (ambiance) {
            ExportBackgroundAmbiance.OLED -> {
                canvas.drawColor(Color.parseColor("#0A0E14"))
            }
            ExportBackgroundAmbiance.CRT_GREEN -> {
                canvas.drawColor(Color.parseColor("#08120C"))
                val scanlinePaint = Paint().apply {
                    color = Color.parseColor("#153FB950")
                    strokeWidth = 2f * scaleMultiplier
                }
                var y = 0f
                while (y < imageHeight) {
                    canvas.drawLine(0f, y, imageWidth.toFloat(), y, scanlinePaint)
                    y += 4f * scaleMultiplier
                }
            }
            ExportBackgroundAmbiance.ALPHA -> {
                canvas.drawColor(Color.TRANSPARENT)
            }
            ExportBackgroundAmbiance.CYBER -> {
                canvas.drawColor(Color.parseColor("#0A0E14"))
                val glowPaint = Paint().apply {
                    shader = RadialGradient(
                        imageWidth / 2f,
                        imageHeight / 2f,
                        max(imageWidth, imageHeight) / 1.5f,
                        intArrayOf(Color.parseColor("#3358A6FF"), Color.parseColor("#15BC8CFF"), Color.TRANSPARENT),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(0f, 0f, imageWidth.toFloat(), imageHeight.toFloat(), glowPaint)
            }
        }

        // 2. Draw ASCII Glyphs
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.MONOSPACE
            textSize = 20f * scaleMultiplier
        }

        for (r in 0 until matrix.rows) {
            val line = if (r < matrix.lines.size) matrix.lines[r] else ""
            val yPos = padding + (r + 1) * baseCharHeight - (4f * scaleMultiplier)

            for (c in 0 until line.length) {
                val glyph = line[c]
                if (glyph == ' ') continue

                val colorInt = if (r < matrix.colors.size && c < matrix.colors[r].size) {
                    matrix.colors[r][c]
                } else {
                    Color.parseColor("#3FB950")
                }

                textPaint.color = colorInt
                val xPos = padding + c * baseCharWidth
                canvas.drawText(glyph.toString(), xPos, yPos, textPaint)
            }
        }

        // 3. Optional Watermark
        if (includeWatermark) {
            val watermarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.MONOSPACE
                textSize = 12f * scaleMultiplier
                color = Color.parseColor("#8B949E")
            }
            val watermarkText = "asciicraft.art • ${matrix.cols}x${matrix.rows} ch"
            canvas.drawText(
                watermarkText,
                padding,
                imageHeight - (15f * scaleMultiplier),
                watermarkPaint
            )
        }

        return bitmap
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
        val filename = "AsciiCraft_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AsciiCraft")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }
        return uri
    }

    fun shareText(context: Context, text: String, subject: String = "ASCII Art") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Partager ASCII Art"))
    }

    fun shareImage(context: Context, bitmap: Bitmap) {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "asciicraft_export_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partager l'image ASCII"))
    }
}
