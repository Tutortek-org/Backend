package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.constants.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.sql.Date
import java.text.SimpleDateFormat

@Component
@Order(3)
class UserProfileSeeder(
    private val userProfileService: UserProfileService,
    private val userService: UserService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.contains(CommandLineArguments.RESEED)) {
                userProfileService.clearUserProfiles()
                val parsedDate = SimpleDateFormat("yyyy-mm-dd").parse("2000-02-03")
                val user = userService.getFirstUser()
                val userProfile = UserProfile(
                    null,
                    "Karolis",
                    "Balciunas",
                    Date(parsedDate.time),
                    5.0F,
                    "Testinis aprasas"
                )
                userProfile.user = user
                userProfileService.saveUserProfile(userProfile)
            }
        }
    }
}
