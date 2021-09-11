package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_generator")
    @SequenceGenerator(name = "payment_generator", sequenceName = "payment_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String
) {
    fun copy(payment: Payment){
        name = payment.name
    }
}