package com.karbal.tutortek.dto.learningMaterialDTO

import com.karbal.tutortek.entities.LearningMaterial

data class LearningMaterialGetDTO(
    val id: Long?,
    val name: String,
    val description: String,
    val link: String,
    val isApproved: Boolean,
    val profileId: Long?
){
    constructor(learningMaterial: LearningMaterial) : this(
        learningMaterial.id,
        learningMaterial.name,
        learningMaterial.description,
        learningMaterial.link,
        learningMaterial.isApproved,
        learningMaterial.meeting.topic.userProfile.id
    )
}
