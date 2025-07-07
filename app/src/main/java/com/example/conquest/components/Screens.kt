package com.example.conquest.components

import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Serializable
data class DetailScreen(
    val name: String?,
    val age: Int
)

@Serializable
object NewCosplayScreen