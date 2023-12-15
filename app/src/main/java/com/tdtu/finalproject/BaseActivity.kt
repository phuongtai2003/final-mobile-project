package com.tdtu.finalproject

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tdtu.finalproject.utils.ContextUtils
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val sharedPref = newBase.getSharedPreferences(newBase.getString(R.string.shared_preferences_key), MODE_PRIVATE)
        val localeToSwitchTo = sharedPref.getString(newBase.getString(R.string.language_key), null)?.let {
            when (it) {
                "en" -> {
                    Locale("en")
                }
                "vi" -> {
                    Locale("vi")
                }
                else -> {
                    Locale("en")
                }
            }
        } ?: Locale("en")
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}