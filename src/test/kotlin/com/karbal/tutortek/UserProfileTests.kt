package com.karbal.tutortek

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.entities.UserProfile
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
class UserProfileTests(
    @Autowired private val userService: UserService,
    @Autowired private val userProfileService: UserProfileService
) {

    private lateinit var latestProfile: UserProfile
    private lateinit var latestUser: User

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        userService.saveUser(user)

        val userProfile = UserProfile(firstName = "Junit", lastName = "Tester")
        val userFromDatabase = userService.getFirstUser()

        userProfile.user = userFromDatabase
        user.userProfile = userProfile
        latestProfile = userProfileService.saveUserProfile(userProfile)
        latestUser = userService.saveUser(userFromDatabase)
    }

    @AfterEach
    fun teardown() {
        userProfileService.clearUserProfiles()
        userService.clearUsers()
    }

    @Test
    fun profileCount() {
        assertThat(userProfileService.getAllUserProfiles().size).isEqualTo(1)
    }

    @Test
    fun profileSave() {
        val userProfile = UserProfile(firstName = "Junit2", lastName = "Tester")
        val userFromDatabase = userService.getFirstUser()
        userProfile.user = userFromDatabase
        userFromDatabase.userProfile = userProfile
        userProfileService.saveUserProfile(userProfile)
        userService.saveUser(userFromDatabase)
        assertThat(userProfileService.getAllUserProfiles().size).isEqualTo(2)
    }

    @Test
    fun profileDelete() {
        latestProfile.id?.let { userProfileService.deleteUserProfile(it) }
        assertThat(userProfileService.getAllUserProfiles().isEmpty())
    }

    @Test
    fun profileGet() {
        val profile = latestProfile.id?.let { userProfileService.getUserProfile(it) }
        assertThat(profile?.get()?.id).isNotNull
    }

    @Test
    fun profileFirst() {
        val profile = userProfileService.getFirstUserProfile()
        assertThat(profile.id).isNotNull
    }

    @Test
    fun profileClear() {
        userProfileService.clearUserProfiles()
        assertThat(userProfileService.getAllUserProfiles().isEmpty())
    }

    fun profileByUserID() {
        val profile = latestUser.id?.let { userProfileService.getUserProfileByUserId(it) }
        assertThat(profile?.id).isNotNull
    }
}
