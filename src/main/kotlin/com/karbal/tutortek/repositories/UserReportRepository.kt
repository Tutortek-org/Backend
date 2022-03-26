package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.UserReport
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserReportRepository : CrudRepository<UserReport, Long> {
    @Query("SELECT * FROM user_reports", nativeQuery = true)
    fun getAllReports(): List<UserReport>
}
