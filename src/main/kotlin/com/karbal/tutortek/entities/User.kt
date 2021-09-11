package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String
){
    fun copy(user: User){
        name = user.name
    }
}
