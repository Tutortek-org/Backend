package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.constants.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Date

@Component
@Order(5)
class MeetingSeeder(
    private val meetingService: MeetingService,
    private val topicService: TopicService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.RESEED)) {
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
                BigDecimal(7.0),
                topic = topic
            ))
        }
    }
}
