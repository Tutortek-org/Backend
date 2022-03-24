package com.karbal.tutortek.dto.topicDTO

import com.karbal.tutortek.entities.Topic

data class TopicGetDTO(
    val id: Long?,
    val name: String,
    val isApproved: Boolean,
    val userId: Long?
){
    constructor(topic: Topic) : this(
        topic.id,
        topic.name,
        topic.isApproved,
        topic.userProfile.id
    )
}
