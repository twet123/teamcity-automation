package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class VcsRootRequestDto(val id: String, val name: String, val vcsName: String, val project: Project, val properties: Properties)