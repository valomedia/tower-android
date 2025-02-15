/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import media.valo.tower_android.BuildConfig
import media.valo.tower_android.model.Gender

//
//  UserHelloSerializable.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

@Serializable
class UserHelloEvent(
    val clientInfo: ClientInfo,
    val userProfile: UserProfile
)

@Serializable
class ClientInfo(
    val identifier: String,
    val version: String
)

@Serializable
class UserProfile(
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val birthdate: String?,
    val phone: String?,
    val email: String?
)

fun serializeUserHello(){
    val clientInfo = ClientInfo(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE.toString())
    val jsonClientInfo = Json.encodeToJsonElement(ClientInfo.serializer(),clientInfo)
    //TODO serialize Userprofile and Userhello, pass serialized Json into SendUserHelloEvent()
}