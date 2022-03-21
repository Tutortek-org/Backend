package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.Payment
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.PaymentService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@Order(7)
class PaymentSeeder(
    private val paymentService: PaymentService,
    private val userProfileService: UserProfileService,
    private val meetingService: MeetingService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.RESEED)) {
            paymentService.clearPayments()
            val user = userProfileService.getFirstUserProfile()
            val meeting = meetingService.getFirstMeeting()
            paymentService.savePayment(Payment(
                null,
                userProfile = user,
                meeting = meeting
            ))
        }
    }
}
