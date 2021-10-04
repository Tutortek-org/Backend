package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.CommandLineArguments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
@Order(1)
class UserLoader(private val userService: UserService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.REPOPULATE)) {
            userService.clearUsers()
            userService.saveUser(User(
                null,
                "populated@email.com",
                BCryptPasswordEncoder().encode("PopulatedPassword")
            ))
        }
    }
}
