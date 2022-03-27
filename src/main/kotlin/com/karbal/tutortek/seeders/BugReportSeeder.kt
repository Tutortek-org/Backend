package com.karbal.tutortek.seeders

import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.entities.BugReport
import com.karbal.tutortek.services.BugReportService
import com.karbal.tutortek.services.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(8)
class BugReportSeeder(
    private val bugReportService: BugReportService,
    private val userService: UserService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                bugReportService.clearBugReports()
                val user = userService.getFirstUser()
                val bugReport = BugReport(null, "Populated name", "Populate description", user)
                bugReportService.saveBugReport(bugReport)
            }
        }
    }
}
