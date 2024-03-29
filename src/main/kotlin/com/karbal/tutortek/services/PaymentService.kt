package com.karbal.tutortek.services

import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.repositories.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class PaymentService(val database: PaymentRepository) {

    fun getAllPayments(): List<Payment> = database.getAllPayments()

    fun savePayment(payment: Payment) = database.save(payment)

    fun deletePayment(id: Long) = database.deleteById(id)

    fun getPayment(id: Long) = database.findById(id)

    fun clearPayments() = database.deleteAll()
}
