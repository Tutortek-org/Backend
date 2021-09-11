package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "meetings")
data class Meeting(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meeting_generator")
    @SequenceGenerator(name = "meeting_generator", sequenceName = "meeting_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String
) {
    fun copy(meeting: Meeting){
        name = meeting.name
    }
}
