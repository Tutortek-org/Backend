package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.bugReportDTO.BugReportGetDTO
import com.karbal.tutortek.dto.bugReportDTO.BugReportPostDTO
import com.karbal.tutortek.entities.BugReport
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.BugReportService
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("bugreports")
class BugReportController(
    private val bugReportService: BugReportService,
    private val userService: UserService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @GetMapping
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun getAllBugReports() = bugReportService.getAllBugReports().map { br -> BugReportGetDTO(br) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBugReport(@RequestBody bugReportPostDTO: BugReportPostDTO, request: HttpServletRequest): BugReportGetDTO {
        verifyDto(bugReportPostDTO)
        val bugReport = convertDtoToEntity(bugReportPostDTO, request)
        return BugReportGetDTO(bugReportService.saveBugReport(bugReport))
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun deleteBugReport(@PathVariable id: Long) {
        val bugReport = bugReportService.getBugReport(id)
        if(bugReport.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.BUG_REPORT_NOT_FOUND)
        bugReportService.deleteBugReport(id)
    }

    private fun convertDtoToEntity(bugReportPostDTO: BugReportPostDTO, request: HttpServletRequest): BugReport {
        val bugReport = BugReport()

        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val email = claims?.get("sub").toString()
        val user = userService.getUserByEmail(email)

        bugReport.name = bugReportPostDTO.name
        bugReport.description = bugReportPostDTO.description
        bugReport.user = user

        return bugReport
    }

    private fun verifyDto(bugReportPostDTO: BugReportPostDTO) {
        if(bugReportPostDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(bugReportPostDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)
    }
}
