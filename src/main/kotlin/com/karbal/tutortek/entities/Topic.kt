package com.karbal.tutortek.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

    @OneToMany(mappedBy = "topic")
    @JsonIgnoreProperties("payments", "topic", "learningMaterials")
    var meetings: List<Meeting> = listOf(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("payments", "topics")
    var user: User = User()
) {
    fun copy(topic: Topic) {
        name = topic.name
        meetings = topic.meetings
        user = topic.user
    }
}
