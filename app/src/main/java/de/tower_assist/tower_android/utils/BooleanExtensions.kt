/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.utils

//
//  BooleanExtensions.kt
//  Tower_Android
//

/**
 * Acts as identity for true Booleans, returns null for false Booleans.
 *
 * @param value The value to maybe return.
 *
 * @return The value if this is true, null otherwise.
 */
infix fun <T> Boolean.then(value: T): T? = if (this) { value } else { null }
