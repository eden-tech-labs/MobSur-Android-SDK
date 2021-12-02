package io.edentechlabs.survey.sdk.data.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class Occurred(
    @SerializedName("max")
    val max: Any?,
    @SerializedName("min")
    val min: Int?
)