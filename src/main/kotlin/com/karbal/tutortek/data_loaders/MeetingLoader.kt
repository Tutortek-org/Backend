package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.sql.Date

@Component
@Order(4)
class MeetingLoader(
    private val meetingService: MeetingService,
    private val topicService: TopicService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            meetingService.clearMeetings()
            val topic = topicService.getFirstTopic()
            val date = Date(System.currentTimeMillis() + 1000000)
            meetingService.saveMeeting(Meeting(
                null,
                "Populated meeting",
                date,
                5,
                "Populated address",
                "Populated description",
                topic = topic
            ))
        }
    }
}
