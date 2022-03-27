package com.karbal.tutortek

import com.karbal.tutortek.entities.User
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
class UserTests(
    @Autowired private val userService: UserService
) {
    private lateinit var latestUser: User

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        latestUser = userService.saveUser(user)
    }

    @AfterEach
    fun teardown() {
        userService.clearUsers()
    }

    @Test
    fun userCount() {
        assertThat(userService.getAllUsers().size).isEqualTo(1)
    }

    @Test
    fun userEmpty() {
        userService.clearUsers()
        assertThat(userService.getAllUsers().isEmpty())
    }

    @Test
    fun userSave() {
        val user = User(email = "junit2@test.com", password = "Junit1234")
        userService.saveUser(user)
        assertThat(userService.getAllUsers().size).isEqualTo(2)
    }

    @Test
    fun userGetByEmail() {
        val user = userService.getUserByEmail("junit@test.com")
        assertThat(user.id).isNotNull
    }

    @Test
    fun userCountByEmail() {
        val count = userService.getUserCountByEmail("junit@test.com")
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun userFirst() {
        val user = userService.getFirstUser()
        assertThat(user.id).isNotNull
    }

    @Test
    fun userGet() {
        val user = latestUser.id?.let { userService.getUserById(it) }
        assertThat(user?.get()?.id).isNotNull
    }

    @Test
    fun userDelete() {
        latestUser.id?.let { userService.deleteUserById(it) }
        assertThat(userService.getAllUsers().isEmpty())
    }
}
