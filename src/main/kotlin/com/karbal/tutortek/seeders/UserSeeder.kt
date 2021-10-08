package com.karbal.tutortek.seeders

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.RoleService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
@Order(2)
class UserSeeder(
    private val userService: UserService,
    private val roleService: RoleService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if(args!!.sourceArgs.contains(CommandLineArguments.RESEED)) {
            userService.clearUsers()
            val user = User(
                null,
                "populated@email.com",
                BCryptPasswordEncoder().encode("PopulatedPassword")
            )
            val role = roleService.getRole(1)
            user.roles.add(role.get())
            userService.saveUser(user)
        }
    }
}
