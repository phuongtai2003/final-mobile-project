package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tdtu.finalproject.constants.Constant
import com.tdtu.finalproject.databinding.ActivityRegisterBinding
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.repository.DataRepository


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var schoolList: ArrayList<String>
    private val dataRepo = DataRepository.getInstance()
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = applicationContext.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)

        schoolList = ArrayList();

        schoolList.addAll(Constant.schollList)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.drop_down_item, schoolList)
        binding.almaMaterSpin.adapter = adapter

        binding.signUpBtn.setOnClickListener{
            val email = binding.registerEmailEdt.text.toString()
            val username = binding.registerUsernameEdt.text.toString()
            val password = binding.registerPasswordEdt.text.toString()
            val confirmPassword = binding.registerConfirmedPasswordEdt.text.toString()
            val almaMater = binding.almaMaterSpin.selectedItem as String
            if(email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(password != confirmPassword){
                Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dataRepo.register(email = email, username = username, password = password, almaMater = almaMater).thenAcceptAsync{
                runOnUiThread{
                    Toast.makeText(this,R.string.register_success, Toast.LENGTH_SHORT).show()
                }
                finish()
            }.exceptionally {
                e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                null
            }
        }

        binding.returnBtn.setOnClickListener{
            finish()
        }
    }
}