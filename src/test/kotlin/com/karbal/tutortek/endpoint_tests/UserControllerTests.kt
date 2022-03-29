package com.karbal.tutortek.endpoint_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.services.RoleService
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
class UserControllerTests(
    @Autowired private val mvc: MockMvc,
    @Autowired private val roleService: RoleService,
    @Autowired private val userService: UserService
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

        mvc.perform(MockMvcRequestBuilders
            .post(SecurityConstants.REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body.toString()))

        val loginJson = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
        }

        mvc.perform(MockMvcRequestBuilders
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
    }

    @Test
    fun createUser() {
        val body = JSONObject().apply {
            put("email", "junit2@test.com")
            put("password", "Junit1234")
            put("role", 0)
        }

        mvc.perform(MockMvcRequestBuilders
            .post(SecurityConstants.REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun login() {
        val loginJson = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
        }

        mvc.perform(MockMvcRequestBuilders
            .post(SecurityConstants.LOGIN_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson.toString()))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteUser() {
        mvc.perform(MockMvcRequestBuilders
            .delete("/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun getAllUsers() {
        mvc.perform(MockMvcRequestBuilders
            .get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun changePassword() {
        val body = JSONObject().apply {
            put("password", "Junit1234")
            put("isBanned", false)
        }

        mvc.perform(MockMvcRequestBuilders
            .put("/password")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun autoLogin() {
        mvc.perform(MockMvcRequestBuilders
            .post("/autologin")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun refreshToken() {
        mvc.perform(MockMvcRequestBuilders
            .get(SecurityConstants.REFRESH_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun assignRole() {
        val user = userService.getFirstUser()

        val body = JSONObject().apply {
            put("userId", user.id)
            put("role", 1)
        }

        mvc.perform(MockMvcRequestBuilders
            .put("/assign")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun banUser() {
        val user = userService.getFirstUser()

        mvc.perform(MockMvcRequestBuilders
            .put("/users/${user.id}/ban")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun unbanUser() {
        val user = userService.getFirstUser()

        mvc.perform(MockMvcRequestBuilders
            .put("/users/${user.id}/unban")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
