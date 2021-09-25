package com.karbal.tutortek.dto.paymentDTO

import com.karbal.tutortek.entities.Payment
import java.math.BigDecimal
import java.sql.Date

data class PaymentGetDTO(
    var price: BigDecimal,
    var date: Date
){
    constructor(payment: Payment) : this(
        payment.price,
        payment.date
    )
}
