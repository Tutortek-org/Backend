package com.karbal.tutortek.entities

import java.math.BigDecimal
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_generator")
    @SequenceGenerator(name = "payment_generator", sequenceName = "payment_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "date", nullable = false)
    var date: Date,

    @Column(name = "price", nullable = false)
    var price: BigDecimal
) {
    fun copy(payment: Payment){
        date = payment.date
        price = payment.price
    }
}
