package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.BugReport
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BugReportRepository : CrudRepository<BugReport, Long> {
    @Query("SELECT * FROM bug_reports", nativeQuery = true)
    fun getAllBugReports(): List<BugReport>
}
