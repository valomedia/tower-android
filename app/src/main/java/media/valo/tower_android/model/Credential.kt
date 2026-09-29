/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

//
//  Credential.kt
//  Tower_Android
//

/**
 * A username and password.
 *
 * @param username The username of the `Credential`.
 * @param password The password of the `Credential`.
 */
data class Credential(
    val username: String,
    val password: String
)
