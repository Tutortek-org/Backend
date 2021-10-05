package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.paymentDTO.PaymentGetDTO
import com.karbal.tutortek.dto.paymentDTO.PaymentPostDTO
import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@RestController
@RequestMapping("payments")
class PaymentController(
    val paymentService: PaymentService,
    val userProfileService: UserProfileService,
    val meetingService: MeetingService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPayment(@RequestBody paymentDTO: PaymentPostDTO): PaymentGetDTO {
        verifyDto(paymentDTO)
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
        verifyDto(paymentDTO)
        val payment = convertDtoToEntity(paymentDTO)
        val paymentInDatabase = paymentService.getPayment(id)

        if(paymentInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.PAYMENT_NOT_FOUND)

        val extractedPayment = paymentInDatabase.get()
        extractedPayment.copy(payment)
        return PaymentGetDTO(paymentService.savePayment(extractedPayment))
    }

    fun convertDtoToEntity(paymentDTO: PaymentPostDTO): Payment {
        val payment = Payment()
        payment.price = paymentDTO.price

        val user = userProfileService.getUserProfile(paymentDTO.userId)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        val meeting = meetingService.getMeeting(paymentDTO.meetingId)
        if(meeting.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        payment.userProfile = user.get()
        payment.meeting = meeting.get()
        return payment
    }

    fun verifyDto(paymentDTO: PaymentPostDTO) {
        if(paymentDTO.price < BigDecimal.ZERO)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NEGATIVE_PRICE)
    }
}
