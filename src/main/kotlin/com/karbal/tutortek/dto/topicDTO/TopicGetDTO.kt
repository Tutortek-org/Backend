package com.karbal.tutortek.dto.topicDTO

import com.karbal.tutortek.entities.Topic

data class TopicGetDTO(
    var name: String
){
    constructor(topic: Topic) : this(
        topic.name
    )
}
