/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
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

@Module
@InstallIn(SingletonComponent::class)
class JsonModule() {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        serializersModule = SerializersModule {
            polymorphic(DataMessage::class) {
                subclass(DataMessage.CapturePhotoRequest::class)
                subclass(DataMessage.CapturePhotoResponse::class)
                subclass(DataMessage.LocationRequest::class)
                subclass(DataMessage.LocationResponse::class)
                subclass(DataMessage.LocationEvent::class)
                subclass(DataMessage.OrientationEvent::class)
                subclass(DataMessage.UserHelloEvent::class)
                subclass(DataMessage.SwitchCameraRequest::class)
                subclass(DataMessage.SwitchCameraResponse::class)
                subclass(DataMessage.ToggleTorchRequest::class)
                subclass(DataMessage.ToggleTorchResponse::class)
            }
            polymorphic(ErrorMessage::class) {
                subclass(ErrorMessage.ErrorEvent::class)
                subclass(ErrorMessage.CapturePhotoResponse::class)
                subclass(ErrorMessage.ToggleTorchResponse::class)
            }
        }
    }

}
