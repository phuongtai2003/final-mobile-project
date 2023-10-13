package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.tdtu.finalproject.databinding.ActivityLoginBinding
import com.tdtu.finalproject.model.User
import com.tdtu.finalproject.repository.DataRepository
import java.util.concurrent.CompletableFuture

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val dataRepo = DataRepository.getInstance()
    private lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)

        binding.returnBtn.setOnClickListener{
            finish()
        }

        binding.loginButton.setOnClickListener{
            binding.loginLoadingIndicator.visibility = View.VISIBLE
            val username = binding.loginUsernameTxt.text.toString()
            val password = binding.loginPasswordTxt.text.toString()
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show()
                binding.loginLoadingIndicator.visibility = View.GONE
                return@setOnClickListener
            }
            dataRepo.login(username, password).thenAcceptAsync{
                val userJson :String =  Gson().toJson(it)
                with(sharedPref.edit()){
                    putString(getString(R.string.user_data_key), userJson)
                    apply()
                }
                val home = Intent(this, HomeActivity::class.java)
                home.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(home)
            }.exceptionally {
                e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                null
            }.whenCompleteAsync{
                _,_ -> binding.loginLoadingIndicator.visibility = View.GONE
            }
        }
    }
}