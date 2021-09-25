package com.karbal.tutortek.dto.learningMaterialDTO

import com.karbal.tutortek.entities.LearningMaterial

data class LearningMaterialGetDTO(
    var name: String,
    var description: String,
    var link: String
){
    constructor(learningMaterial: LearningMaterial) : this(
        learningMaterial.name,
        learningMaterial.description,
        learningMaterial.link
    )
}
