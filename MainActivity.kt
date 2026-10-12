package com.sj.assistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private lateinit var transcript: TextView
    private var ttsReady = false
    private val requestAudio = 42
    private val requestSpeech = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 30, 28, 24)
            setBackgroundColor(0xFFF7F8FC.toInt())
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val title = TextView(this).apply {
            text = "SJ AI"
            textSize = 32f
            setTextColor(0xFF172033.toInt())
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val subtitle = TextView(this).apply {
            text = "তোমার ব্যক্তিগত সহকারী 🤍"
            textSize = 16f
            setTextColor(0xFF626B7C.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        status = TextView(this).apply {
            text = "আমি SJ। নিচের বোতাম চাপ দিয়ে কথা বলো।"
            textSize = 17f
            setTextColor(0xFF172033.toInt())
            gravity = Gravity.CENTER
            setPadding(18, 20, 18, 20)
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        transcript = TextView(this).apply {
            text = "এখানে তোমার কথা ও আমার উত্তর দেখা যাবে।"
            textSize = 16f
            setTextColor(0xFF30394A.toInt())
            setPadding(4, 22, 4, 22)
        }
        val listen = Button(this).apply {
            text = "🎙️  SJ-এর সঙ্গে কথা বলো"
            textSize = 17f
            setOnClickListener { checkPermissionAndListen() }
        }
        val sayHello = Button(this).apply {
            text = "SJ-কে হ্যালো বলো"
            setOnClickListener { respond("হ্যালো! আমি SJ। কীভাবে সাহায্য করতে পারি?") }
        }
        val note = TextView(this).apply {
            text = "নোট: এটি প্রাথমিক সংস্করণ। ‘Hey SJ’ দিয়ে স্ক্রিন বন্ধ অবস্থায় সবসময় জেগে ওঠা এবং অনলাইন AI উত্তর এখনো যোগ করা হয়নি।"
            textSize = 13f
            setTextColor(0xFF70798A.toInt())
            setPadding(4, 20, 4, 0)
        }
        root.addView(title, match())
        root.addView(subtitle, match())
        root.addView(status, match())
        root.addView(transcript, match())
        root.addView(listen, match().apply { topMargin = 12 })
        root.addView(sayHello, match().apply { topMargin = 8 })
        root.addView(note, match().apply { topMargin = 12 })
        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun checkPermissionAndListen() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), requestAudio)
        } else listenNow()
    }

    private fun listenNow() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.text = "এই ফোনে speech recognition পাওয়া যাচ্ছে না। Google app/ভয়েস সার্ভিস আপডেট করে দেখো।"
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "SJ-কে বলো…")
        }
        try { startActivityForResult(intent, requestSpeech) }
        catch (_: Exception) { status.text = "ভয়েস ইনপুট চালু করা গেল না। পরে আবার চেষ্টা করো।" }
    }

    @Deprecated("Deprecated by Android, kept for broad compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestSpeech && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            if (spoken.isNotBlank()) {
                transcript.text = "তুমি: $spoken"
                respond(makeReply(spoken))
            }
        }
    }

    private fun makeReply(input: String): String {
        val q = input.lowercase(Locale.ROOT)
        return when {
            "সময়" in q || "সময়" in q || "কটা বাজে" in q -> "এখন সময় ${SimpleDateFormat("h:mm a", Locale("bn", "IN")).format(Date())}।"
            "তারিখ" in q || "আজ কত" in q -> "আজ ${SimpleDateFormat("d MMMM yyyy", Locale("bn", "IN")).format(Date())}।"
            "হ্যালো" in q || "হাই" in q || "hello" in q || "hey" in q -> "হ্যালো! আমি SJ। বলো, কীভাবে সাহায্য করব?"
            "তোমার নাম" in q || "কে তুমি" in q -> "আমার নাম SJ। আমি তোমার প্রাথমিক AI সহকারী।"
            "ধন্যবাদ" in q -> "স্বাগতম! সাহায্য করতে পেরে ভালো লাগছে।"
            else -> "তুমি বলেছ: $input। এই প্রাথমিক সংস্করণে আমার উত্তর সীমিত। পূর্ণ AI উত্তর দেওয়ার সুবিধা এখনো যোগ করা হয়নি।"
        }
    }

    private fun respond(message: String) {
        status.text = message
        transcript.text = "SJ: $message"
        if (ttsReady) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "sj_reply")
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            ttsReady = true
            val result = tts.setLanguage(Locale("bn", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale("en", "IN"))
            }
            // Voice gender depends on voices installed on the phone.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestAudio && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) listenNow()
        else if (requestCode == requestAudio) Toast.makeText(this, "SJ-এর জন্য মাইক্রোফোন অনুমতি দরকার।", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
        super.onDestroy()
    }
}
