package io.edentechlabs.survey.sdk.data.store

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.google.gson.Gson
import io.edentechlabs.survey.sdk.data.network.dto.Survey
import io.edentechlabs.survey.sdk.utils.Constants.STORE_ID

@Keep
internal class StorageImpl private constructor(context: Context?): StorageProvider {

    private val mPref: SharedPreferences? = context?.getSharedPreferences(STORE_ID, Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    override fun saveSurvey(survey: Survey) {
        val save = gson.toJson(survey)
        save?.let {
            mPref?.edit()
                ?.putString(survey.id.toString(), gson.toJson(survey))
                ?.apply()
        }
    }

    override fun getSurveyById(id: String): Survey? {
        val stringSurvey = mPref?.getString(id, "")
        if(stringSurvey.isNullOrBlank()) {
            return null
        }

        return toSurvey(stringSurvey)
    }

    override fun getAll(): List<Survey> {
        val entries = mPref?.all
        val surveys = mutableListOf<Survey>()
        try {
            if (entries != null) {
                for (survey in entries.entries) {
                    try {
                        surveys.add(toSurvey(survey.value as String))
                    } catch (error: Throwable) {
                        return emptyList()
                    }
                }
                return surveys
            } else {
                return surveys
            }
        } catch (error: Throwable) {
            return listOf()
        }
    }

    override fun removeSurvey(survey: Survey) {
        mPref?.edit()
            ?.remove(survey.id.toString())
            ?.apply()
    }

    override fun clear(): Boolean {
        return mPref?.edit()
            ?.clear()
            ?.commit() ?: false
    }

    private fun toSurvey(value: String): Survey {
        return gson.fromJson(value, Survey::class.java)
    }

    companion object {
        @Synchronized
        fun getInstance(context: Context?): StorageImpl {
            return StorageImpl(context)
        }
    }

}