package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.userReportDTO.UserReportGetDTO
import com.karbal.tutortek.dto.userReportDTO.UserReportPostDTO
import com.karbal.tutortek.entities.UserReport
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.UserReportService
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("userreports")
class UserReportsController(
    private val userReportService: UserReportService,
    private val userService: UserService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReport(@RequestBody userReportPostDTO: UserReportPostDTO, request: HttpServletRequest): UserReportGetDTO {
        verifyDto(userReportPostDTO)
        val userReport = convertDtoToEntity(userReportPostDTO, request)
        return UserReportGetDTO(userReportService.saveUserReport(userReport))
    }

    @GetMapping
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun getAllReports() = userReportService.getAllUserReports().map { ur -> UserReportGetDTO(ur) }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun deleteReport(@PathVariable id: Long) {
        val userReport = userReportService.getUserReport(id)
        if(userReport.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_REPORT_NOT_FOUND)
        userReportService.deleteUserReport(id)
    }

    private fun convertDtoToEntity(userReportPostDTO: UserReportPostDTO, request: HttpServletRequest): UserReport {
        val userReport = UserReport()

        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val userId = claims?.get("uid").toString().toLong()

        val reporter = userService.getUserById(userId)
        if(reporter.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        userReport.reportedBy = reporter.get()

        val reportedUser = userService.getUserById(userReportPostDTO.reportOf)
        if(reportedUser.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        userReport.reportOf = reportedUser.get()

        userReport.description = userReportPostDTO.description
        return userReport
    }

    private fun verifyDto(userReportPostDTO: UserReportPostDTO) {
        if(userReportPostDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)
    }
}
