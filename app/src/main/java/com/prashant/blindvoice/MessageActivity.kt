package com.prashant.blindvoice




import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.util.*


class MessageActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    lateinit var editTextPhone: EditText
    lateinit var send: EditText
    lateinit var sendMsg: Button

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        editTextPhone = findViewById(R.id.editTextPhone)
         send = findViewById(R.id.send)
        sendMsg = findViewById(R.id.sendMsg)
        tts = TextToSpeech(this, this)

        // taking permission from the user

        if (ActivityCompat.checkSelfPermission(
                this,
            android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS), 111
            )
        } else
            receiveMsg()

        // asking to speak message

        send.setOnClickListener {
            tts?.speak("Please speak your message", QUEUE_FLUSH, null, null)
            Thread.sleep(2000)
            speakMsg()
        }
        // asking recipient's phone number

        editTextPhone.setOnClickListener {
            tts?.speak(
                "Please speak recipient's phone number",
                QUEUE_FLUSH,
                null,
                null
            )
            Thread.sleep(2000)
            speakPhone()
        }

        // sending message

        sendMsg.setOnClickListener {
            val sms: SmsManager = SmsManager.getDefault()
            sms.sendTextMessage(
                editTextPhone.text.toString(),
                "ME",
                send.text.toString(),
                null,
                null
            )
            tts?.speak("Message sent successfully", QUEUE_FLUSH, null, null)
        }
    }

    private fun speakMsg() {
        val msgIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        msgIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        msgIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        msgIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
        try {
            startActivityForResult(msgIntent, 101)
        } catch (e: Exception) {
            e.message?.let { Log.e("MSG", it) }
        }
    }

    private fun speakPhone() {
        val phnIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        phnIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        phnIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        phnIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
        try {
            startActivityForResult(phnIntent, 102)
        } catch (e: Exception) {
            e.message?.let { Log.e("Phone", it) }
        }
    }

    private fun receiveMsg() {
        val br = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                        editTextPhone.setText(sms.originatingAddress)
                        send.setText(sms.displayMessageBody)
                        Toast.makeText(applicationContext, "Msg received", Toast.LENGTH_SHORT)
                            .show()
                        val text = """ Message received form${editTextPhone.text}"""
                        tts?.speak(text, QUEUE_FLUSH, null, null)
                        Thread.sleep(5000)
                        tts?.speak(send.text, QUEUE_FLUSH, null, null)
                    }
                }
            }

        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                send.setText(result?.get(0))
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                editTextPhone.setText(result?.get(0))
            }
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
                "Messaging box opened.",
                QUEUE_FLUSH, null, null
            )
        }
    }
}
