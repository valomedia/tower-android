/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.loading

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.BuildConfig
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.Status
import javax.inject.Inject

/**
 * `ViewModel` for `LoadingScreen`.
 *
 * @param towerRepository       `TowerRepository` dependency.
 * @param credentialRepository  `CredentialRepository` dependency.
 * @param profileRepository     `ProfileRepository` dependency.
 */
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    private val credentialRepository: CredentialRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private var indexResponse: IndexResponse? = null

    /**
     * Whether the assistants are currently taking calls.
     *
     * This will be null until connect() is successfully called.
     */
    val isServiceOpen: Boolean?
        get() = when (indexResponse?.openingHours?.status) {
            null -> null
            Status.OPEN -> true
            else -> false
        }

    /**
     * The current opening hours of the service.
     *
     * This will be null until connect() is successfully called.
     */
    val schedule: String?
        get() = indexResponse?.openingHours?.description

    /**
     * The major version of the backend.
     *
     * This will be null until connect() is successfully called and -1 if the version couldn't be parsed.
     */
    val backendMajorVersion: Int?
        get() = if(indexResponse?.apiVersion != null) {
            try {
                indexResponse?.apiVersion?.substringBefore(".")?.toInt()
            } catch (_: Error) {
                -1
            }
        } else { null }

    /**
     * The major version of the app.
     */
    val appMajorVersion
        get() = BuildConfig.VERSION_NAME.substringBefore(".").toInt()

    /**
     * Check whether the user has provided all required profile information.
     */
    suspend fun hasProfile(): Boolean = profileRepository.hasProfile()

    /**
     * Ensure the backend can be reached and register for an identity if necessary.
     *
     * @return Whether the connection was successful.
     */
    suspend fun connect(): Boolean {
        try {
            indexResponse = towerRepository.index()
            if (credentialRepository.getUserId().isNullOrBlank()) {
                credentialRepository.setUserId(towerRepository.registerUser().userId)
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }
}
