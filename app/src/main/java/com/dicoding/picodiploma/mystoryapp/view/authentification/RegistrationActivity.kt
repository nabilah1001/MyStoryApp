package com.dicoding.picodiploma.mystoryapp.view.authentification

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dicoding.picodiploma.mystoryapp.MainActivity
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.api.ApiConfig
import com.dicoding.picodiploma.mystoryapp.api.RegisterResponse
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    private lateinit var activityRegistrationBinding: ActivityRegistrationBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegistrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(activityRegistrationBinding .root)

        playAnimation()

        activityRegistrationBinding.inName.type = "name"
        activityRegistrationBinding.inEmail.type = "email"
        activityRegistrationBinding.inPassword.type = "password"

        activityRegistrationBinding.btnRegister.setOnClickListener {
            val inputName = activityRegistrationBinding.inName.text.toString()
            val inputEmail = activityRegistrationBinding.inEmail.text.toString()
            val inputPassword = activityRegistrationBinding.inPassword.text.toString()

            buatAccount(inputName, inputEmail, inputPassword)
        }
        activityRegistrationBinding.btnLoginBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val addMenu = menu.findItem(R.id.menu_add)
        val logoutMenu = menu.findItem(R.id.menu_logout)

        addMenu.isVisible = false
        logoutMenu.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun buatAccount(inputName: String, inputEmail: String, inputPassword: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().createAccount(inputName, inputEmail, inputPassword)
        client.enqueue(object: Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if(response.isSuccessful && responseBody?.message == "User created") {
                    Toast.makeText(this@RegistrationActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    Toast.makeText(this@RegistrationActivity, getString(R.string.register_fail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure2: ${t.message}")
                Toast.makeText(this@RegistrationActivity, getString(R.string.register_fail), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(activityRegistrationBinding.imgView2, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(activityRegistrationBinding.tvSignup, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(activityRegistrationBinding.inName, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(activityRegistrationBinding.inEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(activityRegistrationBinding.inPassword, View.ALPHA, 1f).setDuration(500)
        val registerButton = ObjectAnimator.ofFloat(activityRegistrationBinding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val question = ObjectAnimator.ofFloat(activityRegistrationBinding.tvQuestion2, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(activityRegistrationBinding.btnLoginBack, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, emailEditTextLayout, passwordEditTextLayout, registerButton, question, loginButton)
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activityRegistrationBinding.progressBar.visibility = View.VISIBLE
        } else {
            activityRegistrationBinding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "Register"
    }
}