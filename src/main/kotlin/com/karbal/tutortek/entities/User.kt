package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.security.Role
import javax.persistence.*

@Entity
@Table(name = "users", indexes = [Index(name = "email_index", columnList = "email", unique = true)])
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

    @Column(name = "isBanned", nullable = false)
    var isBanned: Boolean = false,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var userProfile: UserProfile? = null
) {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<RoleEntity> = mutableSetOf()

    constructor(userPostDTO: UserPostDTO) : this(
        null,
        userPostDTO.email,
        userPostDTO.password
    )
}
