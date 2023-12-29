package org.example.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VcsRootEntryDto(val id: String, @SerialName("vcs-root") val vcsRoot: VcsRoot)