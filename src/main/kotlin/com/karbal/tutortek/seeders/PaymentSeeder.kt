package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.services.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(7)
class PaymentSeeder(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val meetingService: MeetingService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                paymentService.clearPayments()
                val user = userService.getFirstUser()
                val meeting = meetingService.getFirstMeeting()
                paymentService.savePayment(Payment(
                    null,
                    user = user,
                    meeting = meeting
                ))
            }
        }
    }
}
