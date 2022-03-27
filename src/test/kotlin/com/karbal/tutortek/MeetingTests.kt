package com.karbal.tutortek

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserProfileService
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
class MeetingTests(
    @Autowired private val topicService: TopicService,
    @Autowired private val userService: UserService,
    @Autowired private val userProfileService: UserProfileService,
    @Autowired private val meetingService: MeetingService
) {

    private lateinit var latestMeeting: Meeting
    private lateinit var latestTopic: Topic

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        val userFromDatabase = userService.saveUser(user)

        val userProfile = UserProfile(firstName = "Junit", lastName = "Tester")
        userProfile.user = userFromDatabase
        user.userProfile = userProfile
        val profileFromDatabase = userProfileService.saveUserProfile(userProfile)
        userService.saveUser(userFromDatabase)

        val topic = Topic(name = "Test name", description = "Test description", userProfile = profileFromDatabase)
        latestTopic = topicService.saveTopic(topic)

        val meeting = Meeting(topic = topic)
        latestMeeting = meetingService.saveMeeting(meeting)
    }

    @AfterEach
    fun teardown() {
        topicService.clearTopics()
        userProfileService.clearUserProfiles()
        userService.clearUsers()
        meetingService.clearMeetings()
    }

    @Test
    fun meetingCount() {
        assertThat(meetingService.getAllMeetings().size).isEqualTo(1)
    }

    @Test
    fun meetingSave() {
        val meeting = Meeting(topic = latestTopic)
        meetingService.saveMeeting(meeting)
        assertThat(meetingService.getAllMeetings().size).isEqualTo(2)
    }

    @Test
    fun meetingDelete() {
        latestMeeting.id?.let { meetingService.deleteMeeting(it) }
        assertThat(meetingService.getAllMeetings().isEmpty())
    }

    @Test
    fun meetingGet() {
        val meeting = latestMeeting.id?.let { meetingService.getMeeting(it) }
        assertThat(meeting?.get()?.id).isNotNull
    }

    @Test
    fun meetingFirst() {
        val meeting = meetingService.getFirstMeeting()
        assertThat(meeting.id).isNotNull
    }

    @Test
    fun meetingClear() {
        meetingService.clearMeetings()
        assertThat(meetingService.getAllMeetings().isEmpty())
    }
}
