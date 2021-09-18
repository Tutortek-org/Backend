package com.karbal.tutortek.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
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
    @JsonIgnoreProperties("user")
    var payments: List<Payment> = listOf()
){
    fun copy(user: User){
        firstName = user.firstName
        lastName = user.lastName
        birthDate = user.birthDate
        rating = user.rating
    }
}
