package com.prashant.blindvoice

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TimeDateActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    var tts:TextToSpeech?=null
    lateinit var Datetime:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_date)

        Datetime=findViewById(R.id.Datetime)
        tts = TextToSpeech(this, this)
        val currDate = findViewById<TextView>(R.id.currDatetime)
        val date = getCurrentDateTime()
        val dateInString = date.toString("E,dd MMMM yyyy HH:mm:ss")
        currDate.text = dateInString

        Datetime.setOnClickListener {
            tts?.speak(dateInString, TextToSpeech.QUEUE_FLUSH, null, null)
            Thread.sleep(6000)
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
        Log.d(ContentValues.TAG, "Initializing TTS")
        if (p0 == TextToSpeech.SUCCESS) {
            Log.d(ContentValues.TAG, "SUCCESS")
            tts!!.language = Locale.US
            tts?.speak(
                " Date and Time status opened.",
                TextToSpeech.QUEUE_FLUSH, null, null
            )
        }
    }
    private fun Date.toString(format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(this)
    }
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

}