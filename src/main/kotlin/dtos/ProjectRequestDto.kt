package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class ProjectRequestDto(val name: String) {
    val copyAllAssociatedSettings = true
}