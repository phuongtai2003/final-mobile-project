package com.tdtu.finalproject

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.tdtu.finalproject.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var schoolList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        schoolList = ArrayList();

        schoolList.add("TDTU")
        schoolList.add("HCMUS")
        schoolList.add("BKU")

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.drop_down_item, schoolList)
        binding.almaMaterSpin.adapter = adapter


        binding.returnBtn.setOnClickListener{
            finish()
        }
    }
}