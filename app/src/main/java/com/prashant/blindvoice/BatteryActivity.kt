package com.prashant.blindvoice

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.*

class BatteryActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var batteryCard: ImageView
    private var tts: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        batteryCard = findViewById(R.id.batterycard)
        tts = TextToSpeech(this, this)

        val batteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                this.registerReceiver(null, ifilter)

            }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        var isCharging = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Phone is charging"
            else -> "Phone is not charging"
        }

        val txtView = findViewById<TextView>(R.id.batteryStatus)

        txtView.text = isCharging

        val batstate = "Your $isCharging"


        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        val txtview = findViewById<TextView>(R.id.batteryPer)

        txtview.text = batteryPct.toString()

        batteryCard.setOnClickListener {

            val bst = "Your battery level is " + batteryPct.toString() + "percent and $batstate"
            tts?.speak(bst, TextToSpeech.QUEUE_FLUSH, null, null)
        }


    }


    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(p0: Int) {
        Log.d(TAG, "Initializing TTS")
        if (p0 == TextToSpeech.SUCCESS) {
            Log.d(TAG, "SUCCESS")
            tts!!.language = Locale.US
            tts?.speak(
                " Battery status opened.",
                TextToSpeech.QUEUE_FLUSH, null, null
            )
        }
    }
}



