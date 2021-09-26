package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.meetingDTO.MeetingPostDTO
import java.sql.Date
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
    var name: String = "",

    @Column(name = "date", nullable = false)
    var date: Date = Date(System.currentTimeMillis()),

    @Column(name = "maxAttendants", nullable = false)
    var maxAttendants: Int = 0,

    @Column(name = "address", nullable = false)
    var address: String = "",

    @Column(name = "description", nullable = false)
    var description: String = "",

    @OneToMany(mappedBy = "meeting")
    var payments: MutableList<Payment> = mutableListOf(),

    @OneToMany(mappedBy = "meeting")
    var learningMaterials: MutableList<LearningMaterial> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "topic_id")
    var topic: Topic = Topic()
) {
    constructor(meetingPostDTO: MeetingPostDTO) : this(
        null,
        meetingPostDTO.name,
        meetingPostDTO.date,
        meetingPostDTO.maxAttendants,
        meetingPostDTO.address,
        meetingPostDTO.description
    )
//    fun copy(meeting: Meeting){
//        name = meeting.name
//        date = meeting.date
//        maxAttendants = meeting.maxAttendants
//        address = meeting.address
//        description = meeting.description
//        payments = meeting.payments
//        learningMaterials = meeting.learningMaterials
//        topic = meeting.topic
//    }
}
