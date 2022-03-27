package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(4)
class TopicSeeder(
    private val topicService: TopicService,
    private val userProfileService: UserProfileService) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                topicService.clearTopics()
                val user = userProfileService.getFirstUserProfile()
                topicService.saveTopic(Topic(null,
                    "Populated topic",
                    "Populated description",
                    true,
                    userProfile = user
                ))
            }
        }
    }
}
