package com.karbal.tutortek.service_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.entities.UserReport
import com.karbal.tutortek.services.UserReportService
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
class UserReportTests(
    @Autowired private val userReportService: UserReportService,
    @Autowired private val userService: UserService
) {

    private lateinit var latestReport: UserReport
    private lateinit var latestReporter: User
    private lateinit var latestReported: User

    @BeforeEach
    fun setup() {
        val reporter = User(email = "junit1@test.com", password = "Junit1234")
        latestReporter = userService.saveUser(reporter)

        val reported = User(email = "junit2@test.com", password = "Junit1234")
        latestReported = userService.saveUser(reported)

        val report = UserReport(reportedBy = latestReporter, reportOf = latestReported)
        latestReport = userReportService.saveUserReport(report)
    }

    @AfterEach
    fun teardown() {
        userReportService.clearUserReports()
        userService.clearUsers()
    }

    @Test
    fun userReportCount() {
        assertThat(userReportService.getAllUserReports().size).isEqualTo(1)
    }

    @Test
    fun userReportSave() {
        val report = UserReport(reportedBy = latestReporter, reportOf = latestReported)
        userReportService.saveUserReport(report)
        assertThat(userReportService.getAllUserReports().size).isEqualTo(2)
    }

    @Test
    fun userReportDelete() {
        latestReport.id?.let { userReportService.deleteUserReport(it) }
        assertThat(userReportService.getAllUserReports().isEmpty())
    }

    @Test
    fun userReportGet() {
        val report = latestReport.id?.let { userReportService.getUserReport(it) }
        assertThat(report?.get()?.id).isNotNull
    }

    @Test
    fun userReportClear() {
        userReportService.clearUserReports()
        assertThat(userReportService.getAllUserReports().isEmpty())
    }
}
