package io.edentechlabs.survey.sdk.utils

import android.os.Handler
import android.os.Looper

import android.content.Context
import io.edentechlabs.survey.sdk.utils.Constants.DEFAULT_LOCALE
import io.edentechlabs.survey.sdk.utils.Constants.FALLBACK_VERSION
import java.util.*

internal fun Context?.timeout(timeInMil: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        callback()
    }, timeInMil)
}

internal fun Context?.getAppVersion(): String {
    return try {
        this?.packageManager?.getPackageInfo(this.packageName, 0)?.versionName ?: FALLBACK_VERSION
    } catch (error: Throwable) {
        FALLBACK_VERSION
    }
}

internal fun Any?.getLocale(): String {
    return try {
        Locale.getDefault().toString()
    } catch (error: Throwable) {
        DEFAULT_LOCALE
    }
}