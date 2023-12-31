package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import com.google.gson.Gson
import com.tdtu.finalproject.databinding.ActivityLoginBinding
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.ResetPasswordConfirmListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.utils.WrongCredentialsException

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val dataRepo = DataRepository.getInstance()
    private lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)

        binding.returnBtn.setOnClickListener{
            finish()
        }

        binding.forgetPasswordTV.setOnClickListener{
            Utils.showResetPasswordDialog(Gravity.CENTER, this, object : ResetPasswordConfirmListener{
                override fun onConfirm(email: String) {
                    dataRepo.recoverPassword(email).thenAcceptAsync {
                        runOnUiThread {
                            Utils.showSnackBar(binding.root, it)
                        }
                    }.exceptionally {
                        runOnUiThread {
                            Utils.showSnackBar(binding.root, it.message.toString())
                        }
                        null
                    }
                }

                override fun changePassword(oldPassword: String, newPassword: String) {

                }
            })
        }

        binding.loginButton.setOnClickListener{
            binding.loginLoadingIndicator.visibility = View.VISIBLE
            val email = binding.loginUsernameTxt.text.toString()
            val password = binding.loginPasswordTxt.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Utils.showSnackBar(binding.root, getString(R.string.please_fill))
                binding.loginLoadingIndicator.visibility = View.GONE
                return@setOnClickListener
            }
            dataRepo.login(email, password).thenAcceptAsync{
                val userJson :String =  Gson().toJson(it.user)
                val token: String = it.token
                with(sharedPref.edit()){
                    putString(getString(R.string.user_data_key), userJson)
                    putString(getString(R.string.token_key), token)
                    apply()
                }
                val home = Intent(this, HomeActivity::class.java)
                home.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(home)
            }.exceptionally {
                e ->
                binding.loginLoadingIndicator.visibility = View.GONE
                if(e is WrongCredentialsException){
                    Utils.showDialog(Gravity.CENTER, getString(R.string.wrong_credentials), this)
                }
                else{
                    Utils.showDialog(Gravity.CENTER, getString(R.string.something_went_wrong), this)
                }
                null
            }
        }
        setContentView(binding.root)
    }
}