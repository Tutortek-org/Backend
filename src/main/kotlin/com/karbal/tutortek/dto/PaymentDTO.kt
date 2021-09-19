package com.karbal.tutortek.dto

import java.math.BigDecimal

data class PaymentDTO(
    var price: BigDecimal,
    var userId: Long,
    var paymentId: Long
)
