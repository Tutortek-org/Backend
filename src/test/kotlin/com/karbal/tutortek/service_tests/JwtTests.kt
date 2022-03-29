package com.karbal.tutortek.service_tests

import com.karbal.tutortek.TutortekApplication
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.dto.userDTO.UserPutDTO
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.JwtUserDetailsService
import com.karbal.tutortek.services.RoleService
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
class JwtTests(
    @Autowired private val jwtUserDetailsService: JwtUserDetailsService,
    @Autowired private val userService: UserService,
    @Autowired private val roleService: RoleService
) {

    @BeforeEach
    fun setUp() {
        val user = User(email = "junit@test.com", password = "Junit1234")
        userService.saveUser(user)
    }

    @AfterEach
    fun teardown() {
        userService.clearUsers()
    }

    @Test
    fun jwtUpdate() {
        val user = userService.getFirstUser()
        val dto = UserPutDTO("NewPassword", false)
        val updatedUser = jwtUserDetailsService.update(user, dto)
        assertThat(updatedUser.id).isNotNull
    }

    @Test
    fun jwtSave() {
        val role = RoleEntity(0, "ADMIN")
        val dto = UserPostDTO("junit2@test.com", "Junit1234", Role.ADMIN)
        roleService.saveRole(role)
        val user = jwtUserDetailsService.save(dto, role)
        assertThat(user.id).isNotNull
    }

    @Test
    fun jwtLoad() {
        val user = jwtUserDetailsService.loadUserByUsername("junit@test.com")
        assertThat(user.username).isNotNull
    }
}
