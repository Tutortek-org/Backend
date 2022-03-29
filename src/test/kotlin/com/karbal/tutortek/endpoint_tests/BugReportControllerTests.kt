package com.karbal.tutortek.endpoint_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.services.RoleService
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
class BugReportControllerTests(
    @Autowired private val mvc: MockMvc,
    @Autowired private val roleService: RoleService
) {

    private lateinit var latestToken: String
    private var latestBugReportID: Long = 1

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
                latestBugReportID = response.getLong("id")
            }
    }

    @Test
    fun createBugReport() {
        prepareRequest().andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun getAllBugReports() {
        mvc.perform(MockMvcRequestBuilders
            .get("/bugreports")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteBugReport() {
        mvc.perform(MockMvcRequestBuilders
            .delete("/bugreports/$latestBugReportID")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    private fun prepareRequest(): ResultActions {
        val body = JSONObject().apply {
            put("name", "Test")
            put("description", "Test")
        }

        return mvc.perform(MockMvcRequestBuilders
            .post("/bugreports")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
    }
}
