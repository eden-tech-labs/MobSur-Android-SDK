package io.edentechlabs.survey.sdk

import android.content.Context
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import io.edentechlabs.survey.sdk.data.network.dto.SurveyData
import io.edentechlabs.survey.sdk.data.repository.ApiRepository
import io.edentechlabs.survey.sdk.data.repository.StorageRepository
import io.edentechlabs.survey.sdk.data.store.StorageImpl
import io.edentechlabs.survey.sdk.data.store.StorageProvider
import io.edentechlabs.survey.sdk.ui.SurveySheet
import io.edentechlabs.survey.sdk.ui.SurveySheetAction
import io.edentechlabs.survey.sdk.utils.Constants.SHEET_ID
import io.edentechlabs.survey.sdk.utils.getAppVersion
import io.edentechlabs.survey.sdk.utils.timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

@Keep
internal class Bootstrap(
    private val context: WeakReference<Context?>,
    private val appId: String,
    private var userId: String
) : Callback<SurveyData> {

    private var fragmentManager: WeakReference<FragmentManager?>? = null
    private var storage: StorageProvider? = null

    init {
        ApiRepository.getRemoteSurveys(this.appId, this.userId, context.get()?.getAppVersion()).enqueue(this)
        this.storage = StorageImpl.getInstance(context.get())
    }

    fun setFragmentManager(manager: FragmentManager?) {
        this.fragmentManager = WeakReference(manager)
    }

    fun updateUserId(userId: String) {
        this.userId = userId
    }

    fun event(eventName: String) {
        val match =
            StorageRepository.getSurveyByEvent(storage = this.storage, eventName = eventName)

        match?.let { survey ->
            this.fragmentManager?.get()?.let { fManager ->
                val modalBottomSheet =
                    SurveySheet.getInstance(survey, object : SurveySheetAction {
                        override fun onFinished(survey: io.edentechlabs.survey.sdk.data.network.dto.Survey?) {
                            removeFromCache(survey)
                        }

                        override fun onDismiss() {

                        }
                    })
                survey.rules?.get(0)?.delay?.let { delay ->
                    context.get()?.timeout(delay) {
                        modalBottomSheet.show(fManager, SHEET_ID)
                    }
                } ?: modalBottomSheet.show(fManager, SHEET_ID)
            }
                ?: throw Error("Fragment Manager is not available. Call Survey.setFragmentManager before triggering an event")
        }
    }

    private fun removeFromCache(survey: io.edentechlabs.survey.sdk.data.network.dto.Survey?) {
        StorageRepository.removeSurvey(this.storage, survey)
    }

    override fun onResponse(call: Call<SurveyData>, response: Response<SurveyData>) {
        if (response.isSuccessful) {
            response.body()?.data?.let {
                StorageRepository.forceSaveSurveys(this.storage, it)
            }
        }
    }

    override fun onFailure(call: Call<SurveyData>, t: Throwable) {}

}