package com.karbal.tutortek.dto.userReportDTO

import com.karbal.tutortek.dto.userDTO.UserGetDTO
import com.karbal.tutortek.entities.UserReport

data class UserReportGetDTO(
    val id: Long?,
    val description: String,
    val reported: UserGetDTO,
    val reporter: UserGetDTO
) {
    constructor(userReport: UserReport) : this(
        userReport.id,
        userReport.description,
        UserGetDTO(userReport.reportOf),
        UserGetDTO(userReport.reportedBy)
    )
}
