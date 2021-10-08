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

    @OneToMany(mappedBy = "topic", cascade = [CascadeType.REMOVE])
    var meetings: MutableList<Meeting> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User = User()
) {
    fun copy(topic: Topic) {
        name = topic.name
        meetings = topic.meetings
        user = topic.user
    }
}
