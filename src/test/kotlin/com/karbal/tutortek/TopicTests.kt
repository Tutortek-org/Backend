package com.karbal.tutortek

import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.entities.UserProfile
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
class TopicTests(
	@Autowired private val topicService: TopicService,
	@Autowired private val userService: UserService,
	@Autowired private val userProfileService: UserProfileService
) {

	@BeforeEach
	fun setUp() {
		val user = User(email = "junit@test.com", password = "Junit1234")
		userService.saveUser(user)

		val userProfile = UserProfile(firstName = "Junit", lastName = "Tester")
		val userFromDatabase = userService.getFirstUser()
		userProfile.user = userFromDatabase
		user.userProfile = userProfile
		userProfileService.saveUserProfile(userProfile)
		userService.saveUser(userFromDatabase)

		val topic = Topic(name = "Test name", description = "Test description", userProfile = userProfileService.getFirstUserProfile())
		topicService.saveTopic(topic)
	}

	@AfterEach
	fun teardown() {
		topicService.clearTopics()
		userProfileService.clearUserProfiles()
		userService.clearUsers()
	}

	@Test
	fun topicCount() {
		assertThat(topicService.getAllTopics().size).isEqualTo(1)
	}

	@Test
	fun topicEmpty() {
		topicService.clearTopics()
		assertThat(topicService.getAllTopics().isEmpty())
	}

	@Test
	fun topicDelete() {
		val topic = topicService.getFirstTopic()
		topic.id?.let { topicService.deleteTopic(it) }
		assertThat(topicService.getAllTopics().isEmpty())
	}

	@Test
	fun topicGet() {
		val topic = topicService.getTopic(1)
		assertThat(topic.get().name).isEqualTo("Test name")
	}

	@Test
	fun topicSave() {
		val userProfile = userProfileService.getFirstUserProfile()
		topicService.saveTopic(Topic(userProfile = userProfile))
		assertThat(topicService.getAllTopics().size).isEqualTo(2)
	}

	@Test
	fun topicFirst() {
		val topic = topicService.getFirstTopic()
		assertThat(topic.name).isEqualTo("Test name")
	}

	@Test
	fun topicUnapproved() {
		val topics = topicService.getAllUnapproved()
		assertThat(topics.size).isEqualTo(1)
	}
}
