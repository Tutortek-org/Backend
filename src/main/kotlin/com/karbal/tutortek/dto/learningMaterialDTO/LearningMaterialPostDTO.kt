package com.karbal.tutortek.dto.learningMaterialDTO

data class LearningMaterialPostDTO(
    var name: String,
    var description: String,
    var link: String,
    var meetingId: Long
)
