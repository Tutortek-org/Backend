package com.karbal.tutortek.services

import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.repositories.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleService(val database: RoleRepository) {

    fun clearRoles() = database.deleteAll()

    fun saveRole(roleEntity: RoleEntity) = database.save(roleEntity)

    fun getRole(id: Long) = database.findById(id)

    fun deleteRelatedRoles(id: Long) = database.deleteRelatedRoles(id)
}
