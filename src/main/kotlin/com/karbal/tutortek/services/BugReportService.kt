package com.karbal.tutortek.services

import com.karbal.tutortek.entities.BugReport
import com.karbal.tutortek.repositories.BugReportRepository
import org.springframework.stereotype.Service

@Service
class BugReportService(val database: BugReportRepository) {

    fun getAllBugReports(): List<BugReport> = database.getAllBugReports()

    fun saveBugReport(bugReport: BugReport) = database.save(bugReport)

    fun deleteBugReport(id: Long) = database.deleteById(id)

    fun getBugReport(id: Long) = database.findById(id)

    fun clearBugReports() = database.deleteAll()
}
