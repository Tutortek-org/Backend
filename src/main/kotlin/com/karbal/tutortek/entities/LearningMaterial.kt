package com.karbal.tutortek.entities

import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialPostDTO
import javax.persistence.*

@Entity
@Table(name = "learning_materials")
data class LearningMaterial(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "learning_material_generator")
    @SequenceGenerator(name = "learning_material_generator", sequenceName = "learning_material_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description", nullable = false)
    var description: String = "",

    @Column(name = "link", nullable = false)
    var link: String = "",

    @Column(name = "isApproved", nullable = false)
    var isApproved: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    var meeting: Meeting = Meeting()
) {
    constructor(learningMaterialPostDTO: LearningMaterialPostDTO) : this(
        null,
        learningMaterialPostDTO.name,
        learningMaterialPostDTO.description,
        learningMaterialPostDTO.link
    )
}
