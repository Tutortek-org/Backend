package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.userProfileDTO.UserProfilePostDTO
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "user_profiles")
data class UserProfile(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_generator")
    @SequenceGenerator(name = "user_profile_generator", sequenceName = "user_profile_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "firstName", nullable = false)
    var firstName: String = "",

    @Column(name = "lastName", nullable = false)
    var lastName: String = "",

    @Column(name = "birthDate", nullable = false)
    var birthDate: Date = Date(System.currentTimeMillis()),

    @Column(name = "rating", nullable = false)
    var rating: Float = 0.0F,

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.REMOVE])
    var payments: MutableList<Payment> = mutableListOf(),

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.REMOVE])
    var topics: MutableList<Topic> = mutableListOf()
){
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User = User()

    constructor(userProfilePostDTO: UserProfilePostDTO) : this(
        null,
        userProfilePostDTO.firstName,
        userProfilePostDTO.lastName,
        userProfilePostDTO.birthDate,
        userProfilePostDTO.rating
    )

    fun copy(userProfile: UserProfile){
        firstName = userProfile.firstName
        lastName = userProfile.lastName
        birthDate = userProfile.birthDate
        rating = userProfile.rating
        payments = userProfile.payments
        topics = userProfile.topics
        user = userProfile.user
    }
}
