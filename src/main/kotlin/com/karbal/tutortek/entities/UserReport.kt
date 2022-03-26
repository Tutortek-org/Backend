package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "user_reports")
data class UserReport(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_report_generator")
    @SequenceGenerator(name = "user_report_generator", sequenceName = "user_report_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "description", nullable = false)
    var description: String = "",

    @ManyToOne
    @JoinColumn(name = "reported_by_id")
    var reportedBy: User = User(),

    @ManyToOne
    @JoinColumn(name = "report_of_id")
    var reportOf: User = User(),
)
