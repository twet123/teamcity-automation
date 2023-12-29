package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class BuildTypeStep(val name: String, val type: String, val properties: Properties)