package io.edentechlabs.survey.sdk.data.repository

import io.edentechlabs.survey.sdk.data.network.dto.Rule
import io.edentechlabs.survey.sdk.data.network.dto.Survey
import io.edentechlabs.survey.sdk.data.store.StorageProvider
import io.edentechlabs.survey.sdk.utils.Constants.EVENT_TYPE_COUNTED
import java.text.SimpleDateFormat
import java.util.*

internal object StorageRepository {

    private var alreadySeenSurveyInThisSession: Boolean = false

    private val surveyCounter: MutableMap<String, Int> = mutableMapOf()

    private fun clearStorage(storage: StorageProvider?) {
        storage?.clear()
    }

    private fun isInTimeFrame(start: String?, end: String?): Boolean {
        if (start.isNullOrBlank() && end.isNullOrBlank()) {
            return false
        }
        val currentTime = Date()

        if (start.isNullOrBlank()) {
            val endData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(end)
            return currentTime.before(endData)
        }

        if (end.isNullOrBlank()) {
            val startData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(start)
            return currentTime.after(startData)
        }

        val startData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(start)
        val endData = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(end)

        return currentTime.after(startData) && currentTime.before(endData)
    }

    private fun isCompliant(surveyId: String?, rules: Rule?, eventName: String): Boolean {
        val normalizedSurveyId = surveyId ?: "-1"
        rules?.let { rule ->
            if (rule.type == EVENT_TYPE_COUNTED && rule.name == eventName) {
                if (rule.occurred == null || (rule.occurred.max == null && rule.occurred.min == null)) {
                    return true
                }
                if (surveyCounter.containsKey(surveyId)) {
                    surveyCounter[normalizedSurveyId]?.let { currentCount ->
                        rule.occurred.max?.let { max ->
                            if (currentCount == max) {
                                return false
                            }
                        }
                    }
                }
                return true
            } else {
                return false
            }
        } ?: return false
    }

    private fun isMatching(survey: Survey?, eventName: String): Boolean {
        survey?.let {
            if (it.rules.isNullOrEmpty()) {
                return false
            }

            for (rule: Rule? in it.rules) {
                if (isCompliant(survey.id?.toString(), rule, eventName)) {
                    return true
                }
            }

        }

        return false
    }

    fun removeSurvey(storage: StorageProvider?, survey: Survey?) {
        survey?.let {
            surveyCounter.remove(survey.id?.toString())
            storage?.removeSurvey(it)
        }
    }

    fun forceSaveSurveys(storage: StorageProvider?, surveys: List<Survey>) {
        clearStorage(storage)
        for (survey: Survey in surveys) {
            surveyCounter[survey.id.toString()] = 0
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
            if (getSeenStatus()) {
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
                    survey?.id?.toString()?.let { surv ->
                        surveyCounter[surv]?.plus(1)
                            ?.let { it1 -> surveyCounter.put(surv, it1) }
                    }
                    toggleSeen()
                    return survey
                }
            }
        }

        return null
    }
}