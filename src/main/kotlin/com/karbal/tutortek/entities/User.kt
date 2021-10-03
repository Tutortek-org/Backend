package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.userDTO.UserPostDTO
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
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

    @Column(name = "email", nullable = false, unique = true)
    var email: String = "",

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var payments: MutableList<Payment> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var topics: MutableList<Topic> = mutableListOf()

) : UserDetails, Serializable {

    constructor(userPostDTO: UserPostDTO) : this(
        null,
        userPostDTO.firstName,
        userPostDTO.lastName,
        userPostDTO.birthDate,
        userPostDTO.rating,
        userPostDTO.email
    )

    fun copy(user: User){
        firstName = user.firstName
        lastName = user.lastName
        birthDate = user.birthDate
        rating = user.rating
        email = user.email
        payments = user.payments
        topics = user.topics
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }

    override fun getPassword(): String {
        TODO("Not yet implemented")
    }

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
