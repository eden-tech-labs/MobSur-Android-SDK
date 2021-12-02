package io.edentechlabs.survey.sdk.data.repository

import io.edentechlabs.survey.sdk.data.network.dto.Rule
import io.edentechlabs.survey.sdk.data.network.dto.Survey
import io.edentechlabs.survey.sdk.data.store.StorageProvider
import java.text.SimpleDateFormat
import java.util.*

internal object StorageRepository {

    var alreadySeenSurveyInThisSession: Boolean = false

    private fun clearStorage(storage: StorageProvider?) {
        storage?.clear()
    }

    private fun isInTimeFrame(start: String?, end: String?): Boolean {
        if (start.isNullOrBlank() || end.isNullOrBlank()) {
            return false
        }
        val currentTime = Date()
        val startData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(start)
        val endData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(end)


        return currentTime.after(startData) && currentTime.before(endData)
    }

    private fun isMatching(survey: Survey?, eventName: String): Boolean {
        survey?.let {
            if (it.rules.isNullOrEmpty()) {
                return true
            }

            for (rule: Rule? in it.rules) {
                if (rule?.name == eventName) {
                    return true
                }
            }

        }

        return false
    }

    fun removeSurvey(storage: StorageProvider?, survey: Survey?) {
        survey?.let {
            storage?.removeSurvey(it)
        }
    }

    fun forceSaveSurveys(storage: StorageProvider?, surveys: List<Survey>) {
        clearStorage(storage)
        for (survey: Survey in surveys) {
            storage?.saveSurvey(survey)
        }
    }

    private fun toggleSeen() {
        alreadySeenSurveyInThisSession = !alreadySeenSurveyInThisSession
    }

    private fun getSeenStatus(): Boolean {
        return alreadySeenSurveyInThisSession
    }

    fun getSurveyByEvent(storage: StorageProvider?, eventName: String): Survey? {
        storage?.let {
            if(getSeenStatus()) {
                return null
            }
        }
        val allSurveys = storage?.getAll()
        allSurveys?.let {
            for (survey: Survey? in it) {
                if (isInTimeFrame(survey?.startDate, survey?.endDate) && isMatching(
                        survey,
                        eventName
                    )
                ) {
                    toggleSeen()
                    return survey
                }
            }
        }

        return null
    }
}