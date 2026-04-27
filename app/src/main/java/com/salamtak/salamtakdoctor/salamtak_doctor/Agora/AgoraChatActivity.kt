package com.codewithosm.agorasample

import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salamtak.databinding.ActivityAgoraChatBinding
import java.text.SimpleDateFormat
import java.util.*


class AgoraChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgoraChatBinding

    private val USERNAME = "osama"
    private val TOKEN = "007eJxTYDjz0tK/Rptl65LTjw0v1UhsPT1jmrCeQl6J+NmD/eXNT50UGJISjU0MDZPTjBItzE3MjFIsLE2SjQ1SjI0tjAySk8wMjq+oSGkIZGS48eMTKyMDKwMjEIL4KgymhokpScapBroGBhZmuoaGqam6lmlmQJapSbKJhUmiuUWaMQDB8yhU"
    private val APP_KEY ="61956122#1117244"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgoraChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
//        initSDK()
//        initListener()
    }

    private fun initView() {
        binding.tvLog.movementMethod =
            ScrollingMovementMethod()
    }

    // Initializes the SDK.
//    private fun initSDK() {
//        val options = ChatOptions()
//        // Gets your App Key from Agora Console.
//        if (TextUtils.isEmpty(APP_KEY)) {
//            Toast.makeText(
//                this,
//                "You should set your AppKey first!",
//                Toast.LENGTH_SHORT
//            ).show()
//            return
//        }
//        // Sets your App Key to options.
//        options.appKey = APP_KEY
//        // Initializes the Agora Chat SDK.
//        ChatClient.getInstance().init(this, options)
//        // Makes the Agora Chat SDK debuggable.
//        ChatClient.getInstance().setDebugMode(true)
//        // Shows the current user.
//        binding.tvUsername.text = "Current user: $USERNAME"
//    }

//    private fun initListener() {
//        // Adds message event callbacks.
//        ChatClient.getInstance().chatManager().addMessageListener { messages: List<ChatMessage> ->
//            for (message in messages) {
//                val builder = StringBuilder()
//                builder.append("Receive a ").append(message.type.name)
//                    .append(" message from: ").append(message.from)
//                if (message.type == ChatMessage.Type.TXT) {
//                    builder.append(" content:")
//                        .append((message.body as TextMessageBody).message)
//                }
//                showLog(builder.toString(), false)
//            }
//        }
//        // Adds connection event callbacks.
//        ChatClient.getInstance().addConnectionListener(object : ConnectionListener {
//            override fun onConnected() {
//                showLog("onConnected", false)
//            }
//
//            override fun onDisconnected(error: Int) {
//                showLog("onDisconnected: $error", false)
//            }
//
//            override fun onLogout(errorCode: Int) {
//                showLog("User needs to log out: $errorCode", false)
//                ChatClient.getInstance().logout(false, null)
//            }
//
//            // This callback occurs when the token expires. When the callback is triggered, the app client must get a new token from the app server and logs in to the app again.
//            override fun onTokenExpired() {
//                showLog("ConnectionListener onTokenExpired", true)
//            }
//
//            // This callback occurs when the token is about to expire.
//            override fun onTokenWillExpire() {
//                showLog("ConnectionListener onTokenWillExpire", true)
//            }
//        })
//    }

    // Sends the first message.
//    fun sendFirstMessage(view: View?) {
//        val toSendName = binding.etToChatName.text.toString()
//            .trim { it <= ' ' }
//        val content = binding.etToChatName.text.toString().trim { it <= ' ' }
//        // Creates a text message.
//        val message = ChatMessage.createTextSendMessage(content, toSendName)
//        // Sets the message callback before sending the message.
//        message.setMessageStatusCallback(object : CallBack {
//            override fun onSuccess() {
//                showLog("Send message success!", true)
//            }
//
//            override fun onError(code: Int, error: String) {
//                showLog(error, true)
//            }
//        })
//
//        // Sends the message.
//        ChatClient.getInstance().chatManager().sendMessage(message)
//    }


    // Shows logs.
    private fun showLog(content: String, showToast: Boolean) {
        if (TextUtils.isEmpty(content)) {
            return
        }
        runOnUiThread {
            if (showToast) {
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
            }

            val preContent = binding.tvLog.text.toString().trim { it <= ' ' }
            val builder = java.lang.StringBuilder()
            builder.append(
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )
                .append(" ").append(content).append("\n").append(preContent)
            binding.tvLog.text = builder
        }
    }

    // Logs in with a token.
    fun signInWithToken(view: View?) {
        //loginToAgora()
    }

//    private fun loginToAgora() {
//        if (TextUtils.isEmpty(USERNAME) || TextUtils.isEmpty(TOKEN)) {
//            showLog("Username or token is empty!", true)
//            return
//        }
//        ChatClient.getInstance().loginWithAgoraToken(USERNAME, TOKEN, object : CallBack {
//            override fun onSuccess() {
//                showLog("Sign in success!", true)
//            }
//
//            override fun onError(code: Int, error: String) {
//                showLog(error, true)
//            }
//        })
//    }

    // Logs out.
//    fun signOut(view: View?) {
//        if (ChatClient.getInstance().isLoggedInBefore) {
//            ChatClient.getInstance().logout(true, object : CallBack {
//                override fun onSuccess() {
//                    showLog("Sign out success!", true)
//                }
//
//                override fun onError(code: Int, error: String) {
//                    showLog(error, true)
//                }
//            })
//        } else {
//            showLog("You were not logged in", false)
//        }
//    }


}