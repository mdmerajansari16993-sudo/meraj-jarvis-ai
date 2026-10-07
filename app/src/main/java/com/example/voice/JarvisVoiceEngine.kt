package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class JarvisVoiceEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _currentVoiceName = MutableStateFlow("Jarvis Female Neural (Auto)")
    val currentVoiceName: StateFlow<String> = _currentVoiceName.asStateFlow()

    private var onSpeechResultCallback: ((String) -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("JarvisVoiceEngine", "Failed to init TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            configureFemaleNeuralVoice()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            _isReady.value = true
            Log.d("JarvisVoiceEngine", "Neural Female Voice ready.")
        } else {
            Log.w("JarvisVoiceEngine", "TTS initialization failed with status $status")
        }
    }

    private fun configureFemaleNeuralVoice() {
        val t = tts ?: return
        val hiIn = Locale("hi", "IN")
        val enIn = Locale("en", "IN")

        val langResult = t.setLanguage(hiIn)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            t.setLanguage(enIn)
        }

        // Search for highest quality female neural voice
        try {
            val availableVoices = t.voices
            if (!availableVoices.isNullOrEmpty()) {
                val femaleCandidate: Voice? = availableVoices.firstOrNull { v ->
                    val name = v.name.lowercase(Locale.ROOT)
                    (name.contains("female") || name.contains("f00") || name.contains("zira") ||
                            name.contains("hin-female") || name.contains("hie") || name.contains("hic") ||
                            name.contains("end") || name.contains("ene") || name.contains("neural")) &&
                            (v.locale.language == "hi" || v.locale.language == "en")
                } ?: availableVoices.firstOrNull { v ->
                    v.locale.language == "hi" || v.locale.language == "en"
                }

                if (femaleCandidate != null) {
                    t.voice = femaleCandidate
                    _currentVoiceName.value = "Neural Voice: ${femaleCandidate.name}"
                    Log.d("JarvisVoiceEngine", "Selected female neural voice: ${femaleCandidate.name}")
                }
            }
        } catch (e: Exception) {
            Log.w("JarvisVoiceEngine", "Could not query available voices", e)
        }

        // Warm, natural female neural pitch & pacing
        t.setPitch(1.18f)
        t.setSpeechRate(0.98f)
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (tts == null || !_isReady.value) {
            Log.w("JarvisVoiceEngine", "TTS not ready yet. Skipped: $text")
            return
        }
        _isSpeaking.value = true
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "JarvisFemaleUtterance_${System.currentTimeMillis()}")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "JarvisFemaleUtterance")
    }

    fun speakRecommendation() {
        speak("Bhai yeh theek hai, isko buy kar do.")
    }

    fun speakAutomaticModeWelcomeHindi() {
        speak(HINDI_AUTOMATIC_MODE_WELCOME)
    }

    companion object {
        const val HINDI_AUTOMATIC_MODE_WELCOME = "नमस्ते! मैं आपके साथ यहां ऑटोमेटिक काम करने के लिए रेडी हूं। आप जो बोलिएगा मैं वही ऑटोमेटिक कर दूंगा, आपको फोन को छूने की जरूरत नहीं है। बोलिए अब क्या करूं?"
    }

    fun startSpeechRecognition(onResult: (String) -> Unit) {
        onSpeechResultCallback = onResult
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                onResult("Speech recognition not available on this device")
                return
            }

            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _audioRms.value = rmsdB
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        Log.w("JarvisVoiceEngine", "STT Error: $error")
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognized = matches[0]
                            onSpeechResultCallback?.invoke(recognized)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("hi-IN", "en-IN", "en-US"))
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Jarvis is listening...")
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("JarvisVoiceEngine", "Failed to start SpeechRecognizer", e)
            _isListening.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun stop() {
        tts?.stop()
        speechRecognizer?.stopListening()
        _isSpeaking.value = false
        _isListening.value = false
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        speechRecognizer?.destroy()
        speechRecognizer = null
        _isReady.value = false
        _isSpeaking.value = false
        _isListening.value = false
    }
}
