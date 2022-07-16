package com.dicoding.picodiploma.mystoryapp.view.story

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mystoryapp.MainActivity
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.api.ApiConfig
import com.dicoding.picodiploma.mystoryapp.api.ListStoryItem
import com.dicoding.picodiploma.mystoryapp.api.StoryResponse
import com.dicoding.picodiploma.mystoryapp.data.Story
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityListStoryBinding
import com.dicoding.picodiploma.mystoryapp.view.authentification.SharedViewModel
import com.dicoding.picodiploma.mystoryapp.view.authentification.UserPreference
import com.dicoding.picodiploma.mystoryapp.view.authentification.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ListStoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Story Activity"
    }

    private lateinit var storyViewModel: SharedViewModel
    private lateinit var activityStoryBinding: ActivityListStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityStoryBinding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(activityStoryBinding.root)

        setupViewModel()

        val layoutManager = LinearLayoutManager(this)
        activityStoryBinding.rvUsers.layoutManager = layoutManager

        getStories()
    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_add -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                return true
            }

            R.id.menu_logout -> {
                storyViewModel.logout()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun getStories() {
        showLoading(true)

        storyViewModel.getUser().observe(this ) {
            if(it != null) {
                val client = ApiConfig.getApiService().getStories("Bearer " + it.token)
                client.enqueue(object: Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: Response<StoryResponse>
                    ) {
                        showLoading(false)
                        val responseBody = response.body()
                        Log.d(TAG, "onResponse: $responseBody")
                        if(response.isSuccessful && responseBody?.message == "Stories fetched successfully") {
                            setStoriesData(responseBody.listStory)
                            Toast.makeText(this@ListStoryActivity, getString(R.string.success_load_stories), Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "onFailure1: ${response.message()}")
                            Toast.makeText(this@ListStoryActivity, getString(R.string.fail_load_stories), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e(TAG, "onFailure2: ${t.message}")
                        Toast.makeText(this@ListStoryActivity, getString(R.string.fail_load_stories), Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

    }

    private fun setStoriesData(items: List<ListStoryItem>) {
        val listStories = ArrayList<Story>()
        for(item in items) {
            val story = Story(
                item.name,
                item.photoUrl,
                item.description
            )
            listStories.add(story)
        }

        val adapter = StoryAdapter(listStories)
        activityStoryBinding.rvUsers.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activityStoryBinding.progressBar.visibility = View.VISIBLE
        } else {
            activityStoryBinding.progressBar.visibility = View.GONE
        }
    }

}