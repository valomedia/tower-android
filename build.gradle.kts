/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

//
//  build.gradle.kts
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//


/*
 * Top-level build file with configuration options common to all sub-projects/modules.
 */

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.devtools.ksp) apply false
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        // Show warnings about deprecated features.
        options.isDeprecation = true
    }

}

tasks.wrapper {
    // Use the specified gradle version.
    //
    // This task is pulled in as a dependency by :app:preBuild, to ensure the wrapper is always
    // up-to-date with the desired version of gradle.
    gradleVersion = "8.10.2"
}
