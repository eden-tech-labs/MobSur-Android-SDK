package io.edentechlabs.survey.sdk.data.store

import io.edentechlabs.survey.sdk.data.network.dto.Survey

internal interface StorageProvider {
    fun clear(): Boolean
    fun saveSurvey(survey: Survey)
    fun getSurveyById(id: String): Survey?
    fun getAll(): List<Survey>
    fun removeSurvey(survey: Survey)
}