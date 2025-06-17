package com.example.conquest.components

sealed class Paths(val route: String) {
    object MainView: Paths("main_screen")
    object CosplayView: Paths("cosplay_screen")
    object SettingsView: Paths("settings_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {  arg ->
                append("/$arg")
            }
        }
    }
}