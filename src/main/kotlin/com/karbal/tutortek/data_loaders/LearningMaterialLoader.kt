package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(5)
class LearningMaterialLoader(
    private val learningMaterialService: LearningMaterialService,
    private val meetingService: MeetingService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            learningMaterialService.clearLearningMaterials()
            val meeting = meetingService.getFirstMeeting()
            learningMaterialService.saveLearningMaterial(LearningMaterial(
                null,
                "Populated material",
                "Populated description",
                "populated.com/link",
                meeting
            ))
        }
    }
}
