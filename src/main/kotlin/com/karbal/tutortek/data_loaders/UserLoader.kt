package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.User
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
@Order(1)
class UserLoader(private val userService: UserService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            userService.clearUsers()
            val parsedDate = SimpleDateFormat("yyyy-mm-dd").parse("2000-02-03")
            userService.saveUser(User(
                null,
                "Karolis",
                "Balciunas",
                Date(parsedDate.time),
                5.0F,
                "populated@email.com",
                BCryptPasswordEncoder().encode("PopulatedPassword")
            ))
        }
    }
}
