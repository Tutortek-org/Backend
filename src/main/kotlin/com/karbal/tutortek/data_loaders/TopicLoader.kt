package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(3)
class TopicLoader(
    private val topicService: TopicService,
    private val userProfileService: UserProfileService) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            topicService.clearTopics()
            val user = userProfileService.getFirstUserProfile()
            topicService.saveTopic(Topic(null, "Populated topic", userProfile = user))
        }
    }
}
