package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.paymentDTO.PaymentGetDTO
import com.karbal.tutortek.dto.paymentDTO.PaymentPostDTO
import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val meetingService: MeetingService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPayment(@RequestBody paymentDTO: PaymentPostDTO, request: HttpServletRequest): PaymentGetDTO {
        val userFromDatabase = extractUser(request)

        if(userFromDatabase.payments.any { p -> p.meeting.id == paymentDTO.meetingId })
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.USER_ALREADY_SIGNED_UP)

        val payment = convertDtoToEntity(paymentDTO, userFromDatabase)
        return PaymentGetDTO(paymentService.savePayment(payment))
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePayment(@PathVariable id: Long) {
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.PAYMENT_NOT_FOUND)
        paymentService.deletePayment(id)
    }

    @GetMapping
    @Secured(Role.ADMIN_ANNOTATION)
    fun getAllPayments() = paymentService.getAllPayments().map { p -> PaymentGetDTO(p) }

    @GetMapping("{id}")
    fun getPayment(@PathVariable id: Long): PaymentGetDTO {
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.PAYMENT_NOT_FOUND)
        return PaymentGetDTO(payment.get())
    }

    @PutMapping("{id}")
    fun updatePayment(@PathVariable id: Long, @RequestBody paymentDTO: PaymentPostDTO, request: HttpServletRequest): PaymentGetDTO {
        val userFromDatabase = extractUser(request)
        val payment = convertDtoToEntity(paymentDTO, userFromDatabase)
        val paymentInDatabase = paymentService.getPayment(id)

        if(paymentInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.PAYMENT_NOT_FOUND)

        val extractedPayment = paymentInDatabase.get()
        extractedPayment.copy(payment)
        return PaymentGetDTO(paymentService.savePayment(extractedPayment))
    }

    private fun extractUser(request: HttpServletRequest): User {
        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val userId = claims?.get("uid").toString().toLong()
        val user = userService.getUserById(userId)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        return user.get()
    }

    private fun convertDtoToEntity(paymentDTO: PaymentPostDTO, userFromDatabase: User): Payment {
        val meetingFromDatabase = meetingService.getMeeting(paymentDTO.meetingId)
        if(meetingFromDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val payment = Payment().apply {
            user = userFromDatabase
            meeting = meetingFromDatabase.get()
        }
        return payment
    }
}
