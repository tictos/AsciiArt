package com.example.data.repository

import com.example.data.local.AsciiProjectDao
import com.example.data.local.AsciiProjectEntity
import kotlinx.coroutines.flow.Flow

class AsciiProjectRepository(private val dao: AsciiProjectDao) {

    val allProjects: Flow<List<AsciiProjectEntity>> = dao.getAllProjects()
    val favoriteProjects: Flow<List<AsciiProjectEntity>> = dao.getFavoriteProjects()

    suspend fun getProjectById(id: Long): AsciiProjectEntity? {
        return dao.getProjectById(id)
    }

    suspend fun saveProject(project: AsciiProjectEntity): Long {
        return dao.insertProject(project)
    }

    suspend fun updateProject(project: AsciiProjectEntity) {
        dao.updateProject(project)
    }

    suspend fun deleteProject(project: AsciiProjectEntity) {
        dao.deleteProject(project)
    }

    suspend fun deleteProjectById(id: Long) {
        dao.deleteProjectById(id)
    }
}
