package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.constants.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(6)
class LearningMaterialSeeder(
    private val learningMaterialService: LearningMaterialService,
    private val meetingService: MeetingService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                learningMaterialService.clearLearningMaterials()
                val meeting = meetingService.getFirstMeeting()
                learningMaterialService.saveLearningMaterial(LearningMaterial(
                    null,
                    "Populated material",
                    "Populated description",
                    "populated.com/link",
                    true,
                    meeting
                ))
            }
        }
    }
}
