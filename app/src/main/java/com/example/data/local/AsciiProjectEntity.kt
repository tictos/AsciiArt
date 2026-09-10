package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ascii_projects")
data class AsciiProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val asciiContent: String,
    val colorHex: String = "#3FB950", // main accent color
    val cols: Int,
    val rows: Int,
    val glyphCount: Int,
    val category: String, // "Monochrome", "Couleurs ANSI", "Code Snippets", "Favoris"
    val isFavorite: Boolean = false,
    val isFeatured: Boolean = false,
    val dateLabel: String, // "Hier", "2h", "3 oct.", etc.
    val tagSubtitle: String, // "Python Snippet • ANSI 24b", "Shade Ramp", "const array", "Dither 50%"
    val exportFormat: String = "ANSI UTF-8",
    val fileSizeFormatted: String = "1.8 KB",
    val createdAt: Long = System.currentTimeMillis()
)
