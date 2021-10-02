package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.LearningMaterial
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LearningMaterialRepository : CrudRepository<LearningMaterial, Long> {
    @Query("SELECT * FROM learning_materials", nativeQuery = true)
    fun getAllLearningMaterials(): List<LearningMaterial>
}
