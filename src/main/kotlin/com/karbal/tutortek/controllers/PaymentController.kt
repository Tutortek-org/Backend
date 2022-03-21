package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.paymentDTO.PaymentGetDTO
import com.karbal.tutortek.dto.paymentDTO.PaymentPostDTO
import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.security.Role
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("payments")
class PaymentController(
    val paymentService: PaymentService,
    val userProfileService: UserProfileService,
    val meetingService: MeetingService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPayment(@RequestBody paymentDTO: PaymentPostDTO): PaymentGetDTO {
        val payment = convertDtoToEntity(paymentDTO)
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
    fun updatePayment(@PathVariable id: Long, @RequestBody paymentDTO: PaymentPostDTO): PaymentGetDTO {
        val payment = convertDtoToEntity(paymentDTO)
        val paymentInDatabase = paymentService.getPayment(id)

        if(paymentInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.PAYMENT_NOT_FOUND)

        val extractedPayment = paymentInDatabase.get()
        extractedPayment.copy(payment)
        return PaymentGetDTO(paymentService.savePayment(extractedPayment))
    }

    fun convertDtoToEntity(paymentDTO: PaymentPostDTO): Payment {

        val user = userProfileService.getUserProfile(paymentDTO.userId)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        val meetingFromDatabase = meetingService.getMeeting(paymentDTO.meetingId)
        if(meetingFromDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val payment = Payment().apply {
            userProfile = user.get()
            meeting = meetingFromDatabase.get()
        }
        return payment
    }
}
