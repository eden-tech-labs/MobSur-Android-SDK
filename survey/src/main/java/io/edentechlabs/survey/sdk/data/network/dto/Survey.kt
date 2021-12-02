package io.edentechlabs.survey.sdk.data.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class Survey(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("rules")
    val rules: List<Rule>?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("meta")
    val meta: String?
)