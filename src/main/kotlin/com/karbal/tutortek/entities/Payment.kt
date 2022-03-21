package com.karbal.tutortek.entities

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    var meeting: Meeting = Meeting()
) {
    fun copy(payment: Payment){
        date = payment.date
        user = payment.user
        meeting = payment.meeting
    }
}
