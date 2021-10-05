package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.sql.Date
import java.text.SimpleDateFormat

@Component
@Order(2)
class UserProfileLoader(
    private val userProfileService: UserProfileService,
    private val userService: UserService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            userProfileService.clearUserProfiles()

            val parsedDate = SimpleDateFormat("yyyy-mm-dd").parse("2000-02-03")
            val user = userService.getFirstUser()

            userProfileService.saveUserProfile(UserProfile(
                null,
                "Karolis",
                "Balciunas",
                Date(parsedDate.time),
                5.0F,
                user = user
            ))
        }
    }
}