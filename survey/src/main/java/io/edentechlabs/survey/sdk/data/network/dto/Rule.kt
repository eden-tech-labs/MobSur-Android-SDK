package io.edentechlabs.survey.sdk.data.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class Rule(
    @SerializedName("delay")
    val delay: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("occurred")
    val occurred: Occurred?,
    @SerializedName("type")
    val type: String?
)