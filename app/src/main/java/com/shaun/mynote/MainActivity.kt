package com.shaun.mynote


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shaun.mynote.adapter.NoteGridViewAdapter
import com.shaun.mynote.db.Note
import com.shaun.mynote.util.AlertUtil
import com.shaun.mynote.viewmodel.MainViewModel
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {


    private val gridView: GridView by lazy {
        this.findViewById<GridView>(R.id.grid_view)
    }
    private val addButton: FloatingActionButton by lazy {
        this.findViewById<FloatingActionButton>(R.id.button_add)
    }
    private lateinit var adapter: NoteGridViewAdapter
    private lateinit var viewModel: MainViewModel

    private var noteList: List<Note> = listOf()

    private var isAuthentication = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = NoteGridViewAdapter(this)

        val appLifecycleObserver = AppLifecycleObserver(object:
            AppLifecycleObserver.CallbackListener {
            override fun onEnterForeground() {
            }

            override fun onEnterBackground() {
//                isAuthentication = false
//                adapter.updateList(listOf())
            }

        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        gridView.numColumns = 2
        gridView.adapter = adapter
        gridView.horizontalSpacing = 20
        gridView.verticalSpacing = 20
        gridView.onItemClickListener = gridOnItemClickListener

        addButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        if(!isAuthentication) {
            checkBiometric()
        }
    }

    private fun checkBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Toast.makeText(this, "可以使用辨識功能", Toast.LENGTH_LONG).show()
                launchBiometric()

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showAlert("該設備上沒有可用的生物特徵功能")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showAlert("生物識別功能當前不可用")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showAlert("用戶尚未將任何生物識別憑證與其帳戶關聯")

        }
    }

    private fun launchBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showAuthenticationAlert("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthentication = true
                    setViewModel()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showAuthenticationAlert("驗證失敗，重新認證")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        biometricPrompt.authenticate(promptInfo)
        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
//        val biometricLoginButton =
//            findViewById<Button>(R.id.biometric_login)
//        biometricLoginButton.setOnClickListener {
//            biometricPrompt.authenticate(promptInfo)
//        }
    }
    private fun setViewModel() {
        viewModel = MainViewModel(this)

        viewModel.getNoteList().observe(this, Observer {
            Log.d("Text", "xxx : $it")
            adapter.updateList(it)
            noteList = it
        })

        viewModel.getMessage().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.loadAllNote()
    }

    private val gridOnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> run{
        Log.d("OnItemClickListener", "" + position + "")

        val note = noteList.getOrNull(position)
        if(note != null) {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(EditActivity.EXTRA_NOTE_ID, note.uid)
            startActivity(intent)
        }
    }}

    private val showAuthenticationAlert: (message: String) -> Unit= {
        AlertUtil.showAlertDialog(this, it, R.string.button_retry, R.string.button_close, { _: DialogInterface, _: Int ->
            launchBiometric()
        }, { _: DialogInterface, _: Int ->
            exitProcess(0)
        }, false)
    }

    private val showAlert: (message: String) -> Unit= {
        AlertUtil.showAlertDialog(this, it, R.string.button_close, { _: DialogInterface, _: Int ->
            exitProcess(0)
        }, false)
    }
}

