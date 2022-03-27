package com.karbal.tutortek.seeders

import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.entities.UserReport
import com.karbal.tutortek.services.UserReportService
import com.karbal.tutortek.services.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(9)
class UserReportsSeeder(
    private val userService: UserService,
    private val userReportService: UserReportService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                userReportService.clearUserReports()
                val user = userService.getFirstUser()
                val userReport = UserReport(null, "Populated description", user, user)
                userReportService.saveUserReport(userReport)
            }
        }
    }
}
