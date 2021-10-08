package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.RoleEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : CrudRepository<RoleEntity, Long>
