package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AsciiProjectDao {

    @Query("SELECT * FROM ascii_projects ORDER BY isFeatured DESC, createdAt DESC")
    fun getAllProjects(): Flow<List<AsciiProjectEntity>>

    @Query("SELECT * FROM ascii_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): AsciiProjectEntity?

    @Query("SELECT * FROM ascii_projects WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteProjects(): Flow<List<AsciiProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: AsciiProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<AsciiProjectEntity>)

    @Update
    suspend fun updateProject(project: AsciiProjectEntity)

    @Delete
    suspend fun deleteProject(project: AsciiProjectEntity)

    @Query("DELETE FROM ascii_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("SELECT COUNT(*) FROM ascii_projects")
    suspend fun getProjectCount(): Int
}
