package com.karbal.tutortek.endpoint_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.services.RoleService
import com.karbal.tutortek.services.UserService
import org.json.JSONObject
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
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [TutortekApplication::class])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
class UserReportControllerTests(
    @Autowired private val mvc: MockMvc,
    @Autowired private val roleService: RoleService,
    @Autowired private val userService: UserService
) {

    private lateinit var latestToken: String
    private var latestUserReportID: Long = 1

    @BeforeAll
    fun setUp() {
        roleService.saveRole(RoleEntity(1, "ADMIN"))

        val body = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
            put("role", 0)
        }

        mvc.perform(
            MockMvcRequestBuilders
                .post(SecurityConstants.REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
        )

        val loginJson = JSONObject().apply {
            put("email", "junit@test.com")
            put("password", "Junit1234")
        }

        mvc.perform(
            MockMvcRequestBuilders
                .post(SecurityConstants.LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson.toString())
        )
            .andExpect {
                val response = JSONObject(it.response.contentAsString)
                latestToken = response.getString("token")
            }
    }

    @BeforeEach
    fun setUpEach() {
        prepareRequest().andExpect {
                val response = JSONObject(it.response.contentAsString)
                latestUserReportID = response.getLong("id")
            }
    }

    @Test
    fun createUserReport() {
        prepareRequest().andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun getAllReports() {
        mvc.perform(MockMvcRequestBuilders
            .get("/userreports")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteUserReport() {
        mvc.perform(MockMvcRequestBuilders
            .delete("/userreports/$latestUserReportID")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    private fun prepareRequest(): ResultActions {
        val user = userService.getFirstUser()

        val userReportBody = JSONObject().apply {
            put("description", "Test")
            put("reportOf", user.id)
        }

        return mvc.perform(MockMvcRequestBuilders
            .post("/userreports")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(userReportBody.toString()))
    }
}
