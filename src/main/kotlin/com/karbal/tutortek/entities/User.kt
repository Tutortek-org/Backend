package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.userDTO.UserPostDTO
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "email", nullable = false, unique = true)
    var email: String = "",

    @Column(name = "password", nullable = false)
    var password: String = "",

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var userProfile: UserProfile? = null
) {
    constructor(userPostDTO: UserPostDTO) : this(
        null,
        userPostDTO.username,
        userPostDTO.password
    )
}
