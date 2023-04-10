package com.prashant.blindvoice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    View.OnLongClickListener, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    lateinit var msgBox: LinearLayout
    lateinit var phoneMngr: LinearLayout
    lateinit var timeDate: LinearLayout
    lateinit var battery: LinearLayout

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msgBox=findViewById(R.id.msgBox)
        phoneMngr=findViewById(R.id.phoneMngr)
        timeDate=findViewById(R.id.timeDate)
        battery=findViewById(R.id.bttry)

        tts = TextToSpeech(this, this)
        msgBox.setOnClickListener(this)
        phoneMngr.setOnClickListener(this)
        timeDate.setOnClickListener(this)
        battery.setOnClickListener(this)

        msgBox.setOnLongClickListener(this)
        phoneMngr.setOnLongClickListener(this)
        timeDate.setOnLongClickListener(this)
        battery.setOnLongClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {
        val text = when (p0?.id) {
            R.id.msgBox -> {
                "You clicked messaging!"
            }
            R.id.phoneMngr -> {
                "You clicked phone manager!"
            }
            R.id.timeDate -> {
                "You clicked Time/Date  status!"
            }
            R.id.bttry -> {
                "You clicked Battery Status"
            }
            else -> {
                throw IllegalArgumentException("Undefined Clicked")
            }

        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        speak(text)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speak(text: String) {

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onLongClick(p0: View?): Boolean {
        val intent = when (p0?.id) {
            R.id.msgBox -> {
                Intent(this, MessageActivity::class.java)
            }
            R.id.phoneMngr -> {
                Intent(this, PhoneActivity::class.java)
            }
            R.id.timeDate -> {
                Intent(this, TimeDateActivity::class.java)
            }
            R.id.bttry -> {
                Intent(this, BatteryActivity::class.java)
            }
            else -> {
                throw IllegalArgumentException("Undefined Clicked")
            }
        }
        startActivity(intent)
        return true
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {
            tts!!.language = Locale.US
            tts?.speak(
                "Welcome It's BlindVoice to help you,Single tap for details and long press to open an activity.",
                TextToSpeech.QUEUE_FLUSH, null, null
            )
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
}