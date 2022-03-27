package com.karbal.tutortek

import com.karbal.tutortek.entities.*
import com.karbal.tutortek.services.*
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
class PaymentTests(
    @Autowired private val topicService: TopicService,
    @Autowired private val userService: UserService,
    @Autowired private val userProfileService: UserProfileService,
    @Autowired private val meetingService: MeetingService,
    @Autowired private val paymentService: PaymentService
) {

    private lateinit var latestPayment: Payment
    private lateinit var latestMeeting: Meeting
    private lateinit var latestUser: User

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        latestUser = userService.saveUser(user)

        val userProfile = UserProfile(firstName = "Junit", lastName = "Tester")
        userProfile.user = latestUser
        user.userProfile = userProfile
        val profileFromDatabase = userProfileService.saveUserProfile(userProfile)
        userService.saveUser(latestUser)

        val topic = Topic(name = "Test name", description = "Test description", userProfile = profileFromDatabase)
        topicService.saveTopic(topic)

        val meeting = Meeting(topic = topic)
        latestMeeting = meetingService.saveMeeting(meeting)

        val payment = Payment(user = latestUser, meeting = latestMeeting)
        latestPayment = paymentService.savePayment(payment)
    }

    @AfterEach
    fun teardown() {
        topicService.clearTopics()
        userProfileService.clearUserProfiles()
        userService.clearUsers()
        meetingService.clearMeetings()
        paymentService.clearPayments()
    }

    @Test
    fun paymentCount() {
        assertThat(paymentService.getAllPayments().size).isEqualTo(1)
    }

    @Test
    fun paymentSave() {
        val payment = Payment(user = latestUser, meeting = latestMeeting)
        paymentService.savePayment(payment)
        assertThat(paymentService.getAllPayments().size).isEqualTo(2)
    }

    @Test
    fun paymentDelete() {
        latestPayment.id?.let { paymentService.deletePayment(it) }
        assertThat(paymentService.getAllPayments().isEmpty())
    }

    @Test
    fun paymentGet() {
        val payment = latestPayment.id?.let { paymentService.getPayment(it) }
        assertThat(payment?.get()?.id).isNotNull
    }

    @Test
    fun paymentClear() {
        paymentService.clearPayments()
        assertThat(paymentService.getAllPayments().isEmpty())
    }
}
