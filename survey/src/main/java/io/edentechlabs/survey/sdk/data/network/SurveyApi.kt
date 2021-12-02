package io.edentechlabs.survey.sdk.data.network

import androidx.annotation.Keep
import io.edentechlabs.survey.sdk.data.network.dto.SurveyData
import io.edentechlabs.survey.sdk.utils.Constants.BASE_URL
import io.edentechlabs.survey.sdk.utils.Constants.FALLBACK_VERSION
import io.edentechlabs.survey.sdk.utils.Constants.PLATFORM
import io.edentechlabs.survey.sdk.utils.getLocale
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Keep
internal interface SurveyApi {

    @GET("api/surveys")
    fun getSurveys(
        @Query("app_id") appId: String,
        @Query("user_reference_id") userId: String,
        @Query("app_version") appVersion: String = FALLBACK_VERSION,
        @Query("platform") platform: String = PLATFORM,
        @Header("Accept-Language") locale: String = getLocale()
    ): Call<SurveyData>

    companion object {
        fun build(): SurveyApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(SurveyApi::class.java)

        }
    }
}