package com.karbal.tutortek.dto.notificationDTO

import org.json.JSONObject

data class NotificationPostDTO(
    val title: String,
    val content: String
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("title", title)
        json.put("content", content)
        return json.toString()
    }
}
