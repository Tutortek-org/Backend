package com.karbal.tutortek.services

import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.repositories.LearningMaterialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class LearningMaterialService(
    val database: LearningMaterialRepository,
    val entityManager: EntityManager) {

    fun getAllLearningMaterials(): List<LearningMaterial> = database.getAllLearningMaterials()

    fun saveLearningMaterial(learningMaterial: LearningMaterial) = database.save(learningMaterial)

    fun deleteLearningMaterial(id: Long) = database.deleteById(id)

    fun getLearningMaterial(id: Long) = database.findById(id)

    @Transactional
    fun clearLearningMaterials() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate()
        database.clearLearningMaterials()
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate()
    }
}
