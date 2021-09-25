package com.karbal.tutortek.dto.paymentDTO

import java.math.BigDecimal
import java.sql.Date

data class PaymentGetDTO(
    var price: BigDecimal,
    var date: Date
)
