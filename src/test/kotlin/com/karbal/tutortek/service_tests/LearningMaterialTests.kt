package com.karbal.tutortek.service_tests

import com.karbal.tutortek.TutortekApplication
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
class LearningMaterialTests(
    @Autowired private val topicService: TopicService,
    @Autowired private val userService: UserService,
    @Autowired private val userProfileService: UserProfileService,
    @Autowired private val meetingService: MeetingService,
    @Autowired private val learningMaterialService: LearningMaterialService
) {

    private lateinit var latestMaterial: LearningMaterial
    private lateinit var latestMeeting: Meeting

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
        topicService.saveTopic(topic)

        val meeting = Meeting(topic = topic)
        latestMeeting = meetingService.saveMeeting(meeting)

        val learningMaterial = LearningMaterial(meeting = latestMeeting)
        latestMaterial = learningMaterialService.saveLearningMaterial(learningMaterial)
    }

    @AfterEach
    fun teardown() {
        topicService.clearTopics()
        userProfileService.clearUserProfiles()
        userService.clearUsers()
        meetingService.clearMeetings()
        learningMaterialService.clearLearningMaterials()
    }

    @Test
    fun materialCount() {
        assertThat(learningMaterialService.getAllLearningMaterials().size).isEqualTo(1)
    }

    @Test
    fun materialSave() {
        val learningMaterial = LearningMaterial(meeting = latestMeeting)
        learningMaterialService.saveLearningMaterial(learningMaterial)
        assertThat(learningMaterialService.getAllLearningMaterials().size).isEqualTo(2)
    }

    @Test
    fun materialDelete() {
        latestMaterial.id?.let { learningMaterialService.deleteLearningMaterial(it) }
        assertThat(learningMaterialService.getAllLearningMaterials().isEmpty())
    }

    @Test
    fun materialGet() {
        val material = latestMaterial.id?.let { learningMaterialService.getLearningMaterial(it) }
        assertThat(material?.get()?.id).isNotNull
    }

    @Test
    fun materialClear() {
        learningMaterialService.clearLearningMaterials()
        assertThat(learningMaterialService.getAllLearningMaterials().isEmpty())
    }

    @Test
    fun materialUnapproved() {
        assertThat(learningMaterialService.getAllUnapproved().size).isEqualTo(1)
    }
}
