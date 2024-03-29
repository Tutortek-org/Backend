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
class LearningMaterialControllerTests(
    @Autowired private val mvc: MockMvc,
    @Autowired private val roleService: RoleService
) {

    private lateinit var latestToken: String
    private var latestMeetingID: Long = 1
    private var latestTopicID: Long = 1
    private var latestMaterialID: Long = 1

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

        val profileCreateBody = JSONObject().apply {
            put("firstName", "Test")
            put("lastName", "Tester")
            put("birthDate", "2000-02-03")
        }

        mvc.perform(
            MockMvcRequestBuilders
                .post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $latestToken")
                .content(profileCreateBody.toString()))

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

        val topicCreateBody = JSONObject().apply {
            put("name", "Test")
            put("description", "Test")
        }

        mvc.perform(
            MockMvcRequestBuilders
                .post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $latestToken")
                .content(topicCreateBody.toString()))
            .andExpect {
                val response = JSONObject(it.response.contentAsString)
                latestTopicID = response.getLong("id")
            }

        val meetingPostBody = JSONObject().apply {
            put("name", "Test")
            put("date", "2030-01-01")
            put("maxAttendants", 5)
            put("address", "Test")
            put("description", "Test")
            put("price", 5.5)
        }

        mvc.perform(MockMvcRequestBuilders
            .post("/topics/$latestTopicID/meetings")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(meetingPostBody.toString()))
            .andExpect {
                val response = JSONObject(it.response.contentAsString)
                latestMeetingID = response.getLong("id")
            }
    }

    @BeforeEach
    fun setUpEach() {
        prepareRequest().andExpect {
            val response = JSONObject(it.response.contentAsString)
            latestMaterialID = response.getLong("id")
        }
    }

    @Test
    fun createLearningMaterial() {
        prepareRequest().andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun getAllLearningMaterials() {
        mvc.perform(MockMvcRequestBuilders
            .get("/topics/$latestTopicID/meetings/$latestMeetingID/materials")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getSingleLearningMaterial() {
        mvc.perform(MockMvcRequestBuilders
            .get("/topics/$latestTopicID/meetings/$latestMeetingID/materials/$latestMaterialID")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getUnapprovedLearningMaterials() {
        mvc.perform(MockMvcRequestBuilders
            .get("/materials/unapproved")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteLearningMaterial() {
        mvc.perform(MockMvcRequestBuilders
            .delete("/topics/$latestTopicID/meetings/$latestMeetingID/materials/$latestMaterialID")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun updateLearningMaterial() {
        val body = JSONObject().apply {
            put("name", "Test")
            put("description", "Test")
            put("link", "Test")
        }

        mvc.perform(MockMvcRequestBuilders
            .put("/topics/$latestTopicID/meetings/$latestMeetingID/materials/$latestMaterialID")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun prepareRequest(): ResultActions {
        val body = JSONObject().apply {
            put("name", "Test")
            put("description", "Test")
            put("link", "Test")
        }

        return mvc.perform(
            MockMvcRequestBuilders
            .post("/topics/$latestTopicID/meetings/$latestMeetingID/materials")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $latestToken")
            .content(body.toString()))
    }
}
