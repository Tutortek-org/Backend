package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.paymentDTO.PaymentGetDTO
import com.karbal.tutortek.dto.paymentDTO.PaymentPostDTO
import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class PaymentController(val paymentService: PaymentService,
                        val userService: UserService,
                        val meetingService: MeetingService) {

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    fun addPayment(@RequestBody paymentDTO: PaymentPostDTO): PaymentGetDTO {
        val payment = convertDtoToEntity(paymentDTO)
        return PaymentGetDTO(paymentService.savePayment(payment))
    }

    @DeleteMapping("/payments/{id}")
    fun deletePayment(@PathVariable id: Long){
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        paymentService.deletePayment(id)
    }

    @GetMapping("/payments")
    fun getAllPayments() = paymentService.getAllPayments().map { p -> PaymentGetDTO(p) }

    @GetMapping("/payments/{id}")
    fun getPayment(@PathVariable id: Long): PaymentGetDTO {
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        return PaymentGetDTO(payment.get())
    }

    @PutMapping("/payments/{id}")
    fun updatePayment(@PathVariable id: Long, @RequestBody paymentDTO: PaymentPostDTO){
        val payment = convertDtoToEntity(paymentDTO)
        val paymentInDatabase = paymentService.getPayment(id)
        if(paymentInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        val extractedPayment = paymentInDatabase.get()
        extractedPayment.copy(payment)
        paymentService.savePayment(extractedPayment)
    }

    fun convertDtoToEntity(paymentDTO: PaymentPostDTO): Payment {
        val payment = Payment()
        payment.price = paymentDTO.price
        payment.user = userService.getUser(paymentDTO.userId).get()
        payment.meeting = meetingService.getMeeting(paymentDTO.meetingId).get()
        return payment
    }
}
