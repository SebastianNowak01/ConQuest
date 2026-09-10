// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Applied in :app, where the Kotlin sources actually live. Applying them here only ever
    // scanned the (source-free) root project.
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}
