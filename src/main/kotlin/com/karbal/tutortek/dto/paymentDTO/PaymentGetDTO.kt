package com.karbal.tutortek.dto.paymentDTO

import com.karbal.tutortek.entities.Payment
import java.math.BigDecimal
import java.sql.Date

data class PaymentGetDTO(
    val id: Long?,
    val price: BigDecimal,
    val date: Date
){
    constructor(payment: Payment) : this(
        payment.id,
        payment.price,
        payment.date
    )
}
