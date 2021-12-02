package io.edentechlabs.survey

import android.app.Application
import io.edentechlabs.survey.sdk.MobSur

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MobSur.setup(context = this, appId = "52368575-3db1-4cfa-8ecd-15b01fca366b", userId = "12124601")
    }
}