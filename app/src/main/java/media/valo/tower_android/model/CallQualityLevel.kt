package media.valo.tower_android.model

import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import com.azure.android.communication.calling.VideoStreamResolution

enum class CallQualityLevel(val value: Int) {
    VERY_LOW(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VERY_HIGH(5);

    val frameRate: Double
        get() = when (this) {
            VERY_LOW -> 7.5
            LOW -> 15.0
            else -> 30.0
        }

    val resolution: VideoStreamResolution
        get() = when (this) {
            VERY_LOW -> VideoStreamResolution.VGA
            LOW, MEDIUM -> VideoStreamResolution.P540
            HIGH -> VideoStreamResolution.P720
            VERY_HIGH -> VideoStreamResolution.P1080
        }

    val videoStreamFormat: VideoStreamFormat
        get() {
            val format = VideoStreamFormat()
            format.resolution = resolution
            format.pixelFormat = VideoStreamPixelFormat.NV12
            format.framesPerSecond = frameRate.toFloat()

            val resolutionDimensions = when (resolution) {
                VideoStreamResolution.VGA -> 640
                VideoStreamResolution.P540 -> 960
                VideoStreamResolution.P720 -> 1280
                VideoStreamResolution.P1080 -> 1920
                else -> throw IllegalArgumentException("Unknown resolution")
            }
            format.stride1 = resolutionDimensions
            format.stride2 = resolutionDimensions / 2

            return format
        }

    val description: String
        get() = when (this) {
            VERY_LOW -> "Very low"
            LOW -> "Low"
            MEDIUM -> "Medium"
            HIGH -> "High"
            VERY_HIGH -> "Very high"
        }

    val localizedDescription: String
        get() = when (this) {
            VERY_LOW -> "Sehr niedrig"
            LOW -> "Niedrig"
            MEDIUM -> "Mittel"
            HIGH -> "Hoch"
            VERY_HIGH -> "Sehr hoch"
        }

    companion object {
        fun fromValue(value: Int): CallQualityLevel? = values().find { it.value == value }
    }
}