package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader

object SampleImageGenerator {

    fun generateCyberPortrait(): Bitmap {
        val width = 400
        val height = 400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark background with gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                Color.parseColor("#0A0E14"), Color.parseColor("#161B22"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Cyberpunk neon halo
        val haloPaint = Paint().apply {
            shader = RadialGradient(
                width / 2f, height / 2f - 20f, 180f,
                intArrayOf(Color.parseColor("#8058A6FF"), Color.parseColor("#40BC8CFF"), Color.TRANSPARENT),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width / 2f, height / 2f - 20f, 180f, haloPaint)

        // Geometric cyber wolf / feline portrait silhouette
        val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F0F6FC")
            style = Paint.Style.FILL
        }

        val cyanAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#58A6FF")
            style = Paint.Style.FILL
        }

        val greenAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#3FB950")
            style = Paint.Style.FILL
        }

        // Head shape
        val path = Path().apply {
            moveTo(200f, 70f)
            lineTo(260f, 130f)
            lineTo(310f, 80f)
            lineTo(290f, 210f)
            lineTo(330f, 270f)
            lineTo(260f, 320f)
            lineTo(200f, 360f)
            lineTo(140f, 320f)
            lineTo(70f, 270f)
            lineTo(110f, 210f)
            lineTo(90f, 80f)
            lineTo(140f, 130f)
            close()
        }
        canvas.drawPath(path, headPaint)

        // Inner facial facets with cyber colors
        val muzzlePath = Path().apply {
            moveTo(200f, 180f)
            lineTo(240f, 260f)
            lineTo(200f, 330f)
            lineTo(160f, 260f)
            close()
        }
        canvas.drawPath(muzzlePath, cyanAccentPaint)

        // Glowing cyber eyes
        val leftEye = Path().apply {
            moveTo(140f, 190f)
            lineTo(180f, 210f)
            lineTo(150f, 220f)
            close()
        }
        val rightEye = Path().apply {
            moveTo(260f, 190f)
            lineTo(220f, 210f)
            lineTo(250f, 220f)
            close()
        }
        canvas.drawPath(leftEye, greenAccentPaint)
        canvas.drawPath(rightEye, greenAccentPaint)

        // Cyber grid lines across bottom
        val gridPaint = Paint().apply {
            color = Color.parseColor("#30363D")
            strokeWidth = 2f
        }
        for (i in 0..10) {
            val y = 280f + i * 12f
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }

        return bitmap
    }
}
