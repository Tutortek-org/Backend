package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@Order(5)
class PaymentLoader(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val meetingService: MeetingService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            paymentService.clearPayments()
            val user = userService.getFirstUser()
            val meeting = meetingService.getFirstMeeting()
            paymentService.savePayment(Payment(
                null,
                price = BigDecimal(12.34),
                user = user,
                meeting = meeting
            ))
        }
    }
}