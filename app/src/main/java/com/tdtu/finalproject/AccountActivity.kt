package com.tdtu.finalproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.constants.Constant
import com.tdtu.finalproject.databinding.ActivityAccountBinding
import com.tdtu.finalproject.model.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var schoolList : ArrayList<String>
    private lateinit var sharedPref : SharedPreferences
    private val dataRepo: DataRepository = DataRepository.getInstance()
    private val PICK_IMAGE_INTENT = 1

    private var user : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUser()

        schoolList = ArrayList()
        schoolList.addAll(Constant.schollList)

        Picasso.get().load(Uri.parse(user?.profileImage)).into(binding.profileImage)
        sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.drop_down_item, schoolList)
        binding.profileAlmaMater.adapter = adapter
        binding.profileEmailEdt.setText(user?.username)
        binding.profileAlmaMater.setSelection(schoolList.indexOf(user?.almaMater))

        binding.pickImageButton.setOnClickListener{
            val pickingImage = Intent(Intent.ACTION_PICK)
            pickingImage.type = "image/*"
            pickingImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            startActivityForResult(pickingImage, PICK_IMAGE_INTENT)
        }

        binding.saveProfileBtn.setOnClickListener {
            val username = binding.profileEmailEdt.text.toString()
            val almaMater = binding.profileAlmaMater.selectedItem.toString()
            if(username.isEmpty() || almaMater.isEmpty()){
                Utils.showSnackBar(binding.root, getString(R.string.please_fill))
                return@setOnClickListener
            }
            binding.updateProfileProgress.visibility = View.VISIBLE
            binding.profileContent.visibility = View.INVISIBLE
            val token : String? = sharedPref.getString(getString(R.string.token_key), null)
            dataRepo.updateUser(username = username, almaMater = almaMater, id = user?.id!!, token = token!!).thenAcceptAsync{
                with(sharedPref.edit()){
                    val newUserJson = Gson().toJson(it.user)
                    with(sharedPref.edit()){
                        putString(getString(R.string.user_data_key), newUserJson)
                        apply()
                    }
                    user = it.user
                }
                runOnUiThread{
                    binding.profileContent.visibility = View.VISIBLE
                    binding.updateProfileProgress.visibility = View.GONE
                    Utils.showDialog(Gravity.CENTER, it.message, this)
                }
            }.exceptionally { e ->
                runOnUiThread {
                    binding.profileContent.visibility = View.VISIBLE
                    binding.updateProfileProgress.visibility = View.GONE
                    Utils.showDialog(Gravity.CENTER, e.message!!, this)
                }
                null
            }
        }

        binding.returnButton.setOnClickListener{
            val goBack = Intent()
            goBack.putExtra(getString(R.string.user_data_key), user)
            setResult(Activity.RESULT_OK, goBack)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_INTENT && resultCode == Activity.RESULT_OK && data!= null){
            val selectedImageUri = data.data!!
            val inputStream = contentResolver.openInputStream(selectedImageUri)
            val file : File = File(this.cacheDir, "upload_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            val token : String? = sharedPref.getString(getString(R.string.token_key), null)
            binding.profileContent.visibility = View.INVISIBLE
            binding.updateProfileProgress.visibility = View.VISIBLE
            dataRepo.uploadImage(image = file, id = user?.id!!, token = token!!).thenAcceptAsync {
                with(sharedPref.edit()) {
                    val newUserJson = Gson().toJson(it.user)
                    with(sharedPref.edit()) {
                        putString(getString(R.string.user_data_key), newUserJson)
                        apply()
                    }
                    user = it.user
                }
                Log.d("USER TAG", Gson().toJson(user))
                runOnUiThread{
                    binding.profileImage.setImageURI(data.data!!)
                    binding.profileContent.visibility = View.VISIBLE
                    binding.updateProfileProgress.visibility = View.GONE
                    Utils.showDialog(Gravity.CENTER, it.message, this)
                }
            }.exceptionally {
                    e ->
                runOnUiThread {
                    binding.profileContent.visibility = View.VISIBLE
                    binding.updateProfileProgress.visibility = View.GONE
                    Utils.showDialog(Gravity.CENTER, e.message!!, this)
                }
                null
            }
        }
    }

    private fun getUser(){
        user = intent.getParcelableExtra(getString(R.string.user_data_key))
        if(user == null){
            finish()
        }
    }
}