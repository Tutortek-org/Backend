package com.karbal.tutortek.dto

import java.math.BigDecimal

data class PaymentPostDTO(
    var price: BigDecimal,
    var userId: Long,
    var meetingId: Long
)
