package com.karbal.tutortek.services

import com.karbal.tutortek.entities.UserReport
import com.karbal.tutortek.repositories.UserReportRepository
import org.springframework.stereotype.Service

@Service
class UserReportService(val database: UserReportRepository) {

    fun getAllUserReports() = database.getAllReports()

    fun saveUserReport(userReport: UserReport) = database.save(userReport)

    fun deleteUserReport(id: Long) = database.deleteById(id)

    fun getUserReport(id: Long) = database.findById(id)

    fun clearUserReports() = database.deleteAll()
}
