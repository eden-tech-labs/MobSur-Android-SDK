package io.edentechlabs.survey.sdk.data.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SurveyData(
    @SerializedName("data")
    val `data`: List<Survey>?
)