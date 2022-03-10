package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.bugReportDTO.BugReportGetDTO
import com.karbal.tutortek.dto.bugReportDTO.BugReportPostDTO
import com.karbal.tutortek.entities.BugReport
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.BugReportService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("bugreports")
class BugReportController(val bugReportService: BugReportService) {

    @GetMapping
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun getAllBugReports() = bugReportService.getAllBugReports().map { br -> BugReportGetDTO(br) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBugReport(@RequestBody bugReportPostDTO: BugReportPostDTO, request: HttpServletRequest) {
        verifyDto(bugReportPostDTO)

    }

    private fun convertDtoToEntity(bugReportPostDTO: BugReportPostDTO, request: HttpServletRequest): BugReport {
        val bugReport = BugReport()



        return bugReport
    }

    private fun verifyDto(bugReportPostDTO: BugReportPostDTO) {
        if(bugReportPostDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(bugReportPostDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)
    }
}
