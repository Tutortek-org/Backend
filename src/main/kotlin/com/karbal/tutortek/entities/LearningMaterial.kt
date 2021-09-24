package com.karbal.tutortek.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    @JsonIgnoreProperties("learningMaterials")
    var meeting: Meeting = Meeting()
) {
    fun copy(learningMaterial: LearningMaterial){
        name = learningMaterial.name
        description = learningMaterial.description
        link = learningMaterial.link
        meeting = learningMaterial.meeting
    }
}
