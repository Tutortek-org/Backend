package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Payment
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface PaymentRepository : CrudRepository<Payment, Long> {
    @Query("SELECT * FROM payments", nativeQuery = true)
    fun getAllPayments(): List<Payment>

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE payments", nativeQuery = true)
    fun clearPayments()
}