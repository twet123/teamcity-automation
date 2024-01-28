package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class BuildTypeRequestDto(val name: String, val project: Project, val steps: BuildTypeSteps)