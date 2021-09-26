package com.karbal.tutortek.dto.paymentDTO

import java.math.BigDecimal

data class PaymentPostDTO(
    val price: BigDecimal,
    val userId: Long,
    val meetingId: Long
)
