package com.karbal.tutortek.data_loaders

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.sql.Date
import java.text.SimpleDateFormat

@Component
class UserLoader(private val userService: UserService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val parsedDate = SimpleDateFormat("yyyy-mm-dd").parse("2000-02-03")
        userService.saveUser(User(null, "Karolis", "Balciunas", Date(parsedDate.time),5.0F))
    }
}
