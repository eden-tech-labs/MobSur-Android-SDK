package io.edentechlabs.survey

import android.os.Handler
import android.os.Looper

import android.content.Context

fun Context?.timeout(timeInMil: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        callback()
    }, 5000)
}