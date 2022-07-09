package com.tairitsu.compose.arcaea

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Arcaea song metadata
 * The metadata of songs in Arcaea
 *
 * https://github.com/yojohanshinwataikei/vscode-arcaea-file-format/blob/master/json-schema/songdata.json
 */
@Serializable
class MapSet {
    /**
     * The id of the song, used to find the folder containing the aff files and other resources
     */
    var id: String = "Song ID"

    /**
     * The title of the song, localized in various languages
     */
    @SerialName("title_localized")
    var titleLocalized: LocalizedString = LocalizedString("Title")

    /**
     * The numeric id of the song, used in link play to determine the available charts
     */
    var idx: Int = 0

    /**
     * The artist of the song
     */
    var artist: String = "Artist"

    /**
     * The bpm of the song, only used for displaying
     */
    var bpm: String = "60"

    /**
     * The bpm used to calculating the note speed of the chart.
     */
    @SerialName("bpm_base")
    var bpmBase: Double = 60.0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("BPM must be greater than 0")
            }
            field = value
        }

    /**
     * The id of the pack of the song specified in the packlist file
     */
    var set = "Set ID"

    /**
     * The id of purchased item that is needed to play this song, normally is the id of song or pack
     */
    var purchase: String = "Purchase ID"

    /**
     * The start timestamp of song preview, in milliseconds
     */
    var audioPreview: Long = 0

    /**
     * The end timestamp of song preview, in milliseconds
     */
    var audioPreviewEnd: Long = 0

    /**
     * The side of the song, 0 for light and 1 for conflict
     */
    @Serializable(with = MapSetSideSerializer::class)
    var side: Side = Side.LIGHT

    enum class Side(val id: Int) {
        LIGHT(0),
        CONFLICT(1)
    }

    /**
     * The version where the song was published, used to categorize songs by version
     */
    var version: String = "1.0.0"

    /**
     * The Unix timestamp of the time when the song is added, used to sort the songs by time
     */
    var timestamp: Long = 0

    /**
     * The difficulties of the song
     */
    val difficulties: Difficulties = Difficulties()

    /**
     * MapSetSide serializer
     */
    object MapSetSideSerializer : KSerializer<Side> {
        override fun deserialize(decoder: Decoder): Side = when (decoder.decodeInt()) {
            0 -> Side.LIGHT
            1 -> Side.CONFLICT
            else -> throw IllegalArgumentException("Invalid side id")
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("MapSet.Side", Int.serializer().descriptor)

        override fun serialize(encoder: Encoder, value: Side) {
            encoder.encodeInt(value.id)
        }
    }
}
