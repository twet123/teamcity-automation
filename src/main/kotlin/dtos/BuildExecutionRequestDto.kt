package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class BuildExecutionRequestDto(val buildType: BuildExecutionBuildType)