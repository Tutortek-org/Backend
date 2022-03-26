package com.karbal.tutortek.dto.userReportDTO

import com.karbal.tutortek.entities.UserReport

data class UserReportGetDTO(
    val id: Long?,
    val description: String
) {
    constructor(userReport: UserReport) : this(
        userReport.id,
        userReport.description
    )
}
