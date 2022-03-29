package com.karbal.tutortek.endpoint_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.services.RoleService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.services.UserService
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [TutortekApplication::class])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
class UserProfileControllerTests(
    @Autowired private val mvc: MockMvc,
    @Autowired private val roleService: RoleService,
    @Autowired private val userService: UserService,
    @Autowired private val profileService: UserProfileService
) {

    private lateinit var latestToken: String

    @BeforeAll
    fun setUp() {
        roleService.saveRole(RoleEntity(1, "ADMIN"))
        roleService.saveRole(RoleEntity(2, "STUDENT"))
    }

    @BeforeEach
    fun setUpEach() {
        val body = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
            put("role", 0)
        }

        mvc.perform(
            MockMvcRequestBuilders
            .post(SecurityConstants.REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body.toString()))

        val loginJson = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
        }

        mvc.perform(
            MockMvcRequestBuilders
            .post(SecurityConstants.LOGIN_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson.toString()))
            .andExpect {
                val response = JSONObject(it.response.contentAsString)
                latestToken = response.getString("token")
            }
    }

    @AfterEach
    fun teardown() {
        userService.clearUsers()
        profileService.clearUserProfiles()
    }

    @Test
    fun createProfile() {
        val body = JSONObject().apply {
            put("firstName", "Test")
            put("lastName", "Tester")
            put("birthDate", "2000-02-03")
        }

        mvc.perform(MockMvcRequestBuilders
            .post("/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun getAllProfiles() {
        mvc.perform(MockMvcRequestBuilders
            .get("/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getSingleProfile() {
        val body = JSONObject().apply {
            put("firstName", "Test")
            put("lastName", "Tester")
            put("birthDate", "2000-02-03")
        }

        mvc.perform(MockMvcRequestBuilders
            .post("/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))

        val profile = profileService.getFirstUserProfile()

        mvc.perform(MockMvcRequestBuilders
            .get("/profiles/${profile.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteProfile() {
        val body = JSONObject().apply {
            put("firstName", "Test")
            put("lastName", "Tester")
            put("birthDate", "2000-02-03")
        }

        mvc.perform(MockMvcRequestBuilders
            .post("/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))

        val profile = profileService.getFirstUserProfile()

        mvc.perform(MockMvcRequestBuilders
            .delete("/profiles/${profile.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun updateProfile() {
        val body = JSONObject().apply {
            put("firstName", "Test")
            put("lastName", "Tester")
            put("birthDate", "2000-02-03")
        }

        mvc.perform(MockMvcRequestBuilders
            .post("/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))

        val profile = profileService.getFirstUserProfile()
        body.apply {
            put("rating", 3.5)
            put("description", "Test description")
        }

        mvc.perform(MockMvcRequestBuilders
            .put("/profiles/${profile.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
