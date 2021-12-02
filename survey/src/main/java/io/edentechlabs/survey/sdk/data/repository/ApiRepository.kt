package io.edentechlabs.survey.sdk.data.repository

import io.edentechlabs.survey.sdk.data.network.SurveyApi
import io.edentechlabs.survey.sdk.data.network.dto.SurveyData
import io.edentechlabs.survey.sdk.utils.Constants.FALLBACK_VERSION
import retrofit2.Call

internal object ApiRepository {

    private fun getApi(): SurveyApi {
        return SurveyApi.build()
    }

    fun getRemoteSurveys(appId: String, userId: String, appVersion: String?): Call<SurveyData> {
        return getApi().getSurveys(appId, userId, appVersion = appVersion ?: FALLBACK_VERSION)
    }

}