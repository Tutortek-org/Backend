package com.karbal.tutortek

import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.services.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TutortekApplicationTests {

	@Autowired
	lateinit var topicService: TopicService

	@Autowired
	lateinit var userService: UserService

	@Autowired
	lateinit var userProfileService: UserProfileService

	@BeforeAll
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

	@Test
	fun topicServiceTest() {
		assertThat(topicService.getAllTopics().size).isEqualTo(1)
	}
}
