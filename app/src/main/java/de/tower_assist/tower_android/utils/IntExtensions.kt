/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.utils

//
//  IntExtensions.kt
//  Tower_Android
//

/**
 * Whether the `Int` is divisible by two.
 */
val Int.isEven: Boolean get() = this % 2 == 0

/**
 * Whether the `Int` isn't divisible by two.
 */
val Int.isOdd: Boolean get() = !isEven
