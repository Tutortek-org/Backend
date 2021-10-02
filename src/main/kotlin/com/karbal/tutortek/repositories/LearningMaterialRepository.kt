package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.LearningMaterial
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface LearningMaterialRepository : CrudRepository<LearningMaterial, Long> {
    @Query("SELECT * FROM learning_materials", nativeQuery = true)
    fun getAllLearningMaterials(): List<LearningMaterial>

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE learning_materials", nativeQuery = true)
    fun clearLearningMaterials()
}
