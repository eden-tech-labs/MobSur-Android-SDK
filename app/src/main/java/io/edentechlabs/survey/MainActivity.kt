package io.edentechlabs.survey

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.edentechlabs.survey.sdk.MobSur

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobSur.setFragmentManager(supportFragmentManager)
        val triggerBtn = findViewById<Button>(R.id.event_trigger)
        triggerBtn?.setOnClickListener {
            MobSur.event("App_Launched")
        }
    }
}