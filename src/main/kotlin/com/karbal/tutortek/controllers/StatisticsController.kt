package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.statisticsDTO.StatisticsGetDTO
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("statistics")
class StatisticsController(
    private val topicService: TopicService,
    private val userService: UserService
) {

    companion object {
        val systemStartupTime = System.currentTimeMillis()
    }

    @GetMapping
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun getStatistics(): StatisticsGetDTO {
        val numberOfTopics = topicService.getAllTopics().size
        val numberOfUsers = userService.getAllUsers().size
        val systemUpTime = System.currentTimeMillis() - systemStartupTime
        return StatisticsGetDTO(numberOfTopics, numberOfUsers, systemUpTime)
    }
}
