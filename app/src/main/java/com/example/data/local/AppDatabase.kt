package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AsciiProjectEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun asciiProjectDao(): AsciiProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asciicraft_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).asciiProjectDao().insertProjects(getSampleProjects())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        fun getSampleProjects(): List<AsciiProjectEntity> {
            val now = System.currentTimeMillis()
            return listOf(
                AsciiProjectEntity(
                    id = 1,
                    title = "Cyber_Cat.ascii",
                    asciiContent = """
             /\_/\             
            ( o.o )            
             > ^ <             
         [CYBER-CAT]          
         /|  ===  |\          
        / |       | \         
       (_/ ======= \_)        
          █▓▒░░▒▓█            
                    """.trimIndent(),
                    colorHex = "#3FB950",
                    cols = 64,
                    rows = 32,
                    glyphCount = 2048,
                    category = "Couleurs ANSI",
                    isFavorite = true,
                    isFeatured = true,
                    dateLabel = "2h",
                    tagSubtitle = "Python Snippet • ANSI 24b",
                    fileSizeFormatted = "1.8 KB",
                    createdAt = now
                ),
                AsciiProjectEntity(
                    id = 2,
                    title = "Neon_Skull.txt",
                    asciiContent = """
        .---.        
       /     \       
      | () () |      
       \  ^  /       
        |||||        
                    """.trimIndent(),
                    colorHex = "#58A6FF",
                    cols = 80,
                    rows = 40,
                    glyphCount = 3200,
                    category = "Monochrome",
                    isFavorite = true,
                    isFeatured = false,
                    dateLabel = "Hier",
                    tagSubtitle = "Standard Ramp",
                    fileSizeFormatted = "2.4 KB",
                    createdAt = now - 86400000L
                ),
                AsciiProjectEntity(
                    id = 3,
                    title = "Mona_Blocks",
                    asciiContent = """
       ░▒▓███▓▒░      
      ░▓███████▓░     
      ░▓█(.)(.)█▓░    
      ░▓███ █▓██░     
       ░▒▓███▓▒░      
                    """.trimIndent(),
                    colorHex = "#F0F6FC",
                    cols = 120,
                    rows = 50,
                    glyphCount = 6000,
                    category = "Monochrome",
                    isFavorite = false,
                    isFeatured = false,
                    dateLabel = "3 oct.",
                    tagSubtitle = "Shade Ramp",
                    fileSizeFormatted = "4.1 KB",
                    createdAt = now - 172800000L
                ),
                AsciiProjectEntity(
                    id = 4,
                    title = "Terminal_Logo.rs",
                    asciiContent = """
      <[ RUST ]>      
     /========\      
    |   0x4F   |     
     \========/      
      `'I I I'`      
                    """.trimIndent(),
                    colorHex = "#D29922",
                    cols = 48,
                    rows = 24,
                    glyphCount = 1152,
                    category = "Code Snippets",
                    isFavorite = false,
                    isFeatured = false,
                    dateLabel = "28 sep.",
                    tagSubtitle = "const array",
                    fileSizeFormatted = "1.4 KB",
                    createdAt = now - 345600000L
                ),
                AsciiProjectEntity(
                    id = 5,
                    title = "Sunset_80s",
                    asciiContent = """
        .----.        
       /  |   \       
      /   |    \      
    ==--======--==    
    ///////////////   
                    """.trimIndent(),
                    colorHex = "#58A6FF",
                    cols = 60,
                    rows = 30,
                    glyphCount = 1800,
                    category = "Couleurs ANSI",
                    isFavorite = false,
                    isFeatured = false,
                    dateLabel = "15 sep.",
                    tagSubtitle = "Dither 50%",
                    fileSizeFormatted = "1.9 KB",
                    createdAt = now - 600000000L
                )
            )
        }
    }
}
