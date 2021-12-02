package io.edentechlabs.survey.sdk

import android.content.Context
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference
import java.util.*

@Keep
object MobSur {

    private var bootstrap: Bootstrap? = null

    /**
     * This method is always the first one to be called. It sets the storage
     * and initializes the ETL surveys
     *
     * @param  context
     * @param  appId the unique identifier of your app
     * @param  userId user identifier
     */
    fun setup(context: Context?, appId: String, userId: String) {
        bootstrap = Bootstrap(WeakReference(context), appId, userId)
    }

    /**
     * This method allow an update on already existing user identifier
     *
     * @param  userId the updated value for the user id
     */
    fun updateUserId(userId: String) {
        bootstrap?.updateUserId(userId)
            ?: throw Error("You need to call the initialize method, before updateUserId()")
    }

    /**
     * This method should be called before triggering an event.
     * It is used to setup the BottomSheetDialog.
     *
     * @param  fragmentManager
     * @see BottomSheetDialogFragment
     */
    fun setFragmentManager(fragmentManager: FragmentManager?) {
        bootstrap?.setFragmentManager(fragmentManager)
            ?: throw Error("You need to call the initialize method, before setFragmentManager()")
    }

    /**
     * This method is used to trigger an even and potentially open a survey
     *
     * @param  eventName
     */
    fun event(eventName: String) {
        bootstrap?.event(eventName)
            ?: throw Error("You need to call the initialize method, before event()")
    }

}