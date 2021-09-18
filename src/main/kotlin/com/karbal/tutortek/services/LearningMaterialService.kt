package com.karbal.tutortek.services

import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.repositories.LearningMaterialRepository
import org.springframework.stereotype.Service

@Service
class LearningMaterialService(val database: LearningMaterialRepository) {

    fun getAllLearningMaterials(): List<LearningMaterial> = database.getAllLearningMaterials()

    fun saveLearningMaterial(learningMaterial: LearningMaterial) = database.save(learningMaterial)

    fun deleteLearningMaterial(id: Long) = database.deleteById(id)

    fun getLearningMaterial(id: Long) = database.findById(id)
}
