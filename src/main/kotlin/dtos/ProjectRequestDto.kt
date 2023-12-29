package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class ProjectRequestDto(val name: String, val id: String) {
    val copyAllAssociatedSettings = true
}