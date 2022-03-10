package com.karbal.tutortek.entities

import javax.persistence.*

@Entity
@Table(name = "bug_reports")
data class BugReport(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bug_report_generator")
    @SequenceGenerator(name = "bug_report_generator", sequenceName = "bug_report_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description", nullable = false)
    var description: String = "",

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User = User()
)
