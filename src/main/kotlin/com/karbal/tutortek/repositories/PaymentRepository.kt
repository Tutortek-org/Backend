package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Payment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : CrudRepository<Payment, Long> {
    @Query("SELECT * FROM payments", nativeQuery = true)
    fun getAllPayments(): List<Payment>
}