package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.userDTO.UserPostDTO
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "firstName", nullable = false)
    var firstName: String = "",

    @Column(name = "lastName", nullable = false)
    var lastName: String = "",

    @Column(name = "birthDate", nullable = false)
    var birthDate: Date = Date(System.currentTimeMillis()),

    @Column(name = "rating", nullable = false)
    var rating: Float = 0.0F,

    @OneToMany(mappedBy = "user")
    var payments: MutableList<Payment> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var topics: MutableList<Topic> = mutableListOf()
){
    constructor(userPostDTO: UserPostDTO) : this(
        null,
        userPostDTO.firstName,
        userPostDTO.lastName,
        userPostDTO.birthDate,
        userPostDTO.rating
    )

    fun copy(user: User){
        firstName = user.firstName
        lastName = user.lastName
        birthDate = user.birthDate
        rating = user.rating
        payments = user.payments
        topics = user.topics
    }
}
