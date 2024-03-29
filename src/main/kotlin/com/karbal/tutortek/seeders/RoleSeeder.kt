package com.karbal.tutortek.seeders

import com.karbal.tutortek.constants.CommandLineArguments
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.RoleService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class RoleSeeder(private val roleService: RoleService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        args?.let {
            if(it.sourceArgs.any { sa -> sa == CommandLineArguments.RESEED || sa == CommandLineArguments.DB_ROLES }) {
                roleService.deleteAllRecordsFromUserRole()
                roleService.clearRoles()
                Role.values().forEach { r ->
                    val id = r.ordinal + 1L
                    roleService.saveRole(RoleEntity(id, r.name))
                }
            }
        }
    }
}
