/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.model.ErrorMessage
import javax.inject.Singleton

//
//  JsonModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

@Module
@InstallIn(SingletonComponent::class)
class JsonModule() {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        serializersModule = SerializersModule {
            polymorphic(DataMessage::class) {
                subclass(DataMessage.LocationRequest::class)
                subclass(DataMessage.LocationResponse::class)
                subclass(DataMessage.LocationEvent::class)
                subclass(DataMessage.OrientationEvent::class)
                subclass(DataMessage.UserHelloEvent::class)
                subclass(DataMessage.SwitchCameraRequest::class)
                subclass(DataMessage.SwitchCameraResponse::class)
            }
            polymorphic(ErrorMessage::class) {
                subclass(ErrorMessage.ErrorEvent::class)
            }
        }
    }

}
