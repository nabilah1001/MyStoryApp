package com.dicoding.picodiploma.mystoryapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mystoryapp.api.ApiConfig
import com.dicoding.picodiploma.mystoryapp.api.LoginResponse
import com.dicoding.picodiploma.mystoryapp.data.User
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.mystoryapp.view.authentification.RegistrationActivity
import com.dicoding.picodiploma.mystoryapp.view.authentification.SharedViewModel
import com.dicoding.picodiploma.mystoryapp.view.authentification.UserPreference
import com.dicoding.picodiploma.mystoryapp.view.authentification.ViewModelFactory
import com.dicoding.picodiploma.mystoryapp.view.story.ListStoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: SharedViewModel
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setupViewModel()
        playAnimation()

        activityMainBinding.inEmail.type = "email"
        activityMainBinding.inPassword.type = "password"

        activityMainBinding.btnLogin.setOnClickListener {
            val inputEmail = activityMainBinding.inEmail.text.toString()
            val inputPassword = activityMainBinding.inPassword.text.toString()

            masukAccount(inputEmail, inputPassword)
        }

        activityMainBinding.btnGoRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if(user.isLogin) {
                val intent = Intent(this, ListStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
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

    private fun masukAccount(inputEmail: String, inputPassword: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().login(inputEmail, inputPassword)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)

                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if(response.isSuccessful && responseBody?.message == "success") {
                    mainViewModel.saveUser(User(responseBody.loginResult.token, true))
                    Toast.makeText(this@MainActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, ListStoryActivity::class.java)

                    startActivity(intent)
                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    Toast.makeText(this@MainActivity, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure2: ${t.message}")
                Toast.makeText(this@MainActivity, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }

        })
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(activityMainBinding.imgView1, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(activityMainBinding.tvSignup, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(activityMainBinding.tvDescription, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(activityMainBinding.inEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(activityMainBinding.inPassword, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(activityMainBinding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val question = ObjectAnimator.ofFloat(activityMainBinding.tvQuestion2, View.ALPHA, 1f).setDuration(500)
        val registerButton = ObjectAnimator.ofFloat(activityMainBinding.btnGoRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, emailEditTextLayout, passwordEditTextLayout, loginButton, question, registerButton)
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activityMainBinding.progressBar2.visibility = View.VISIBLE
        } else {
            activityMainBinding.progressBar2.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "Main Activity"
    }
}
