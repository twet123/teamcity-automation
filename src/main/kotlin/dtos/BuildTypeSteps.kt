package org.example.dtos

import kotlinx.serialization.Serializable

@Serializable
class BuildTypeSteps(val step: ArrayList<BuildTypeStep>)