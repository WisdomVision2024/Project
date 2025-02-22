package Class

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class VoiceToTextParse(
    private val app:Application
):RecognitionListener {

    private val _state= MutableStateFlow(VoiceToTextParseState())
    val state=_state.asStateFlow()
    private val recognizer=SpeechRecognizer.createSpeechRecognizer(app)
    init {
        Log.d("VoiceToTextParse", "VoiceToTextParse initialized with app: $app")
    }
    fun startListening(){
        _state.update { VoiceToTextParseState() }

        if (!SpeechRecognizer.isRecognitionAvailable(app)){
            _state.update {
                it.copy(error = "Recognition is available.")
            }
        }

        val systemLocale = Locale.getDefault().toLanguageTag()
        Log.d("VoiceToTextParse","systemLocale=$systemLocale")
        val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,systemLocale)
        }

        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _state.update {
            it.copy(
                isSpeaking = true
            )
        }
        Log.d("VoiceToTextParse","Start to listen")
    }
    fun stopListening(){
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
        recognizer.stopListening()
        Log.d("VoiceToTextParse","Stop to listen")
    }
    override fun onReadyForSpeech(params: Bundle?) {
        _state.update {
            it.copy(
                error=null
            )
        }
        Log.d("VoiceToTextParse","Ready")
    }

    override fun onBeginningOfSpeech() =Unit

    override fun onRmsChanged(rmsdB: Float) =Unit

    override fun onBufferReceived(buffer: ByteArray?)=Unit

    override fun onEndOfSpeech() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        if (error != SpeechRecognizer.ERROR_CLIENT) {
            Log.e("VoiceToTextParse", "Speech recognition error code: $error")
            _state.update {
                it.copy(
                    error = "Error: $error"
                )
            }
        }
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull( 0)
            ?.let { result->
                _state.update {
                    it.copy(
                        spokenText = result
                    )
                }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) =Unit

    override fun onEvent(eventType: Int, params: Bundle?)=Unit
}
data class VoiceToTextParseState(
    val spokenText:String="",
    val isSpeaking:Boolean=false,
    val error:String?=null
)