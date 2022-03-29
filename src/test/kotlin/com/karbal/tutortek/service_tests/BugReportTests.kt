package com.karbal.tutortek.service_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.entities.BugReport
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.BugReportService
import com.karbal.tutortek.services.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [TutortekApplication::class])
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
class BugReportTests(
    @Autowired private val bugReportService: BugReportService,
    @Autowired private val userService: UserService
) {

    private lateinit var latestReport: BugReport
    private lateinit var latestUser: User

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        latestUser = userService.saveUser(user)

        val bugReport = BugReport(user = latestUser)
        latestReport = bugReportService.saveBugReport(bugReport)
    }

    @AfterEach
    fun teardown() {
        bugReportService.clearBugReports()
        userService.clearUsers()
    }

    @Test
    fun bugReportCount() {
        assertThat(bugReportService.getAllBugReports().size).isEqualTo(1)
    }

    @Test
    fun bugReportSave() {
        val bugReport = BugReport(user = latestUser)
        bugReportService.saveBugReport(bugReport)
        assertThat(bugReportService.getAllBugReports().size).isEqualTo(2)
    }

    @Test
    fun bugReportDelete() {
        latestReport.id?.let { bugReportService.deleteBugReport(it) }
        assertThat(bugReportService.getAllBugReports().isEmpty())
    }

    @Test
    fun bugReportGet() {
        val report = latestReport.id?.let { bugReportService.getBugReport(it) }
        assertThat(report?.get()?.id).isNotNull
    }

    @Test
    fun bugReportClear() {
        bugReportService.clearBugReports()
        assertThat(bugReportService.getAllBugReports().isEmpty())
    }
}
