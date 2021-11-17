package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.RoleEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RoleRepository : CrudRepository<RoleEntity, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM user_role WHERE user_id = :user_id", nativeQuery = true)
    fun deleteRelatedRoles(@Param("user_id") id: Long)

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE user_role", nativeQuery = true)
    fun deleteAllRecordsFromUserRole()
}
