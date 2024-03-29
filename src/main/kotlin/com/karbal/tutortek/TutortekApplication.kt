package com.karbal.tutortek

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class TutortekApplication {
	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
	runApplication<TutortekApplication>(*args)
}
