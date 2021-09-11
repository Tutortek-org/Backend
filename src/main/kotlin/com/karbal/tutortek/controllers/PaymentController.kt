package com.karbal.tutortek.controllers

import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class PaymentController(val paymentService: PaymentService) {

    @PostMapping("/payments/add")
    fun addPayment(@RequestBody payment: Payment) = paymentService.savePayment(payment)

    @DeleteMapping("/payments/{id}")
    fun deletePayment(@PathVariable id: Long){
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        paymentService.deletePayment(id)
    }

    @GetMapping("/payments/all")
    fun getAllPayments() = paymentService.getAllPayments()

    @GetMapping("/payments/{id}")
    fun getPayment(@PathVariable id: Long): Optional<Payment> {
        val payment = paymentService.getPayment(id)
        if(payment.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        return payment
    }

    @PutMapping("/payments/{id}")
    fun updatePayment(@PathVariable id: Long, @RequestBody payment: Payment){
        val paymentInDatabase = paymentService.getPayment(id)
        if(paymentInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")
        val extractedPayment = paymentInDatabase.get()
        extractedPayment.copy(payment)
        paymentService.savePayment(extractedPayment)
    }
}