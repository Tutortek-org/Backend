package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.security.Role
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
@Order(2)
class UserSeeder(private val userService: UserService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.RESEED)) {
            userService.clearUsers()
            userService.saveUser(User(
                null,
                "populated@email.com",
                BCryptPasswordEncoder().encode("PopulatedPassword")
                //Role.ADMIN
            ))
        }
    }
}
