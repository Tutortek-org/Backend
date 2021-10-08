package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "roles")
data class RoleEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = ""

) {
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles", cascade = [CascadeType.REMOVE])
    var users: MutableSet<User> = mutableSetOf()
}