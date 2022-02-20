package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "topics")
data class Topic(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_generator")
    @SequenceGenerator(name = "topic_generator", sequenceName = "topic_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "isApproved", nullable = false)
    var isApproved: Boolean = false,

    @OneToMany(mappedBy = "topic", cascade = [CascadeType.REMOVE])
    var meetings: MutableList<Meeting> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    var userProfile: UserProfile = UserProfile()
) {
    fun copy(topic: Topic) {
        name = topic.name
        isApproved = topic.isApproved
        meetings = topic.meetings
        userProfile = topic.userProfile
    }
}
