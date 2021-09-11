package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String
){
    fun copy(user: User){
        name = user.name
    }
}
