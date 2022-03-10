package com.karbal.tutortek.dto.bugReportDTO

import com.karbal.tutortek.entities.BugReport

data class BugReportGetDTO(
    val name: String,
    val description: String
) {
    constructor(bugReport: BugReport) : this(
        bugReport.name,
        bugReport.description
    )
}
