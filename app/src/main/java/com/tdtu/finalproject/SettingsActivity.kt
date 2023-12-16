package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tdtu.finalproject.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        binding.returnBtn.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }

        sharedPreferences.getString(getString(R.string.language_key), null)?.let {
            when (it) {
                "en" -> {
                    binding.englishOption.isChecked = true
                }
                "vi" -> {
                    binding.vietnameseOption.isChecked = true
                }
                else -> {
                    binding.englishOption.isChecked = true
                }
            }
        }

        binding.englishOption.setOnClickListener {
            sharedPreferences.edit().putString(getString(R.string.language_key), "en").apply()
            switchLanguage("en")
        }

        binding.vietnameseOption.setOnClickListener {
            sharedPreferences.edit().putString(getString(R.string.language_key), "vi").apply()
            switchLanguage("vi")
        }

        setContentView(binding.root)
    }

    private fun switchLanguage(languageCode: String) {
        val activityRes = resources
        val activityConf = activityRes.configuration
        val newLocale = Locale(languageCode)
        activityConf.setLocale(newLocale)
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

        val applicationRes = applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf,
            applicationRes.displayMetrics
        )
        recreate()
    }
}