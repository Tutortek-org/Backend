package com.karbal.tutortek.controllers

import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class LearningMaterialController(val learningMaterialService: LearningMaterialService) {

    @GetMapping("/materials/all")
    fun getAllLearningMaterials() = learningMaterialService.getAllLearningMaterials()

    @GetMapping("/materials/{id}")
    fun getLearningMaterial(@PathVariable id: Long): Optional<LearningMaterial> {
        val learningMaterial = learningMaterialService.getLearningMaterial(id)
        if(learningMaterial.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        return learningMaterial
    }

    @PostMapping("/materials/add")
    fun addLearningMaterial(@RequestBody learningMaterial: LearningMaterial) = learningMaterialService.saveLearningMaterial(learningMaterial)

    @DeleteMapping("/materials/{id}")
    fun deleteLearningMaterial(@PathVariable id: Long) {
        val learningMaterial = learningMaterialService.getLearningMaterial(id)
        if(learningMaterial.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        learningMaterialService.deleteLearningMaterial(id)
    }

    @PutMapping("/materials/{id}")
    fun updateLearningMaterial(@PathVariable id: Long, @RequestBody learningMaterial: LearningMaterial){
        val learningMaterialInDatabase = learningMaterialService.getLearningMaterial(id)
        if(learningMaterialInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        val extractedLearningMaterial = learningMaterialInDatabase.get()
        extractedLearningMaterial.copy(learningMaterial)
        learningMaterialService.saveLearningMaterial(extractedLearningMaterial)
    }
}
