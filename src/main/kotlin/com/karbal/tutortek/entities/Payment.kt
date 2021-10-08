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
    var date: Date = Date(System.currentTimeMillis()),

    @Column(name = "price", nullable = false)
    var price: BigDecimal = BigDecimal(0.0),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    var meeting: Meeting = Meeting()
) {
    fun copy(payment: Payment){
        date = payment.date
        price = payment.price
        user = payment.user
        meeting = payment.meeting
    }
}
