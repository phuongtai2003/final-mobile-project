package com.tdtu.finalproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tdtu.finalproject.constants.Constant
import com.tdtu.finalproject.databinding.FragmentProfileBinding
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener
import com.tdtu.finalproject.utils.UpdateUserModelListener
import com.tdtu.finalproject.viewmodel.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userViewModel : UserViewModel
    private var _binding : FragmentProfileBinding? = null
    private lateinit var schoolList : ArrayList<String>
    private val dataRepo: DataRepository = DataRepository.getInstance()
    private var updateUserModelListener : UpdateUserModelListener? = null
    private lateinit var sharedPref : SharedPreferences
    private val PICK_IMAGE_INTENT = 1
    private var onDrawerNavigationPressedListener: OnDrawerNavigationPressedListener? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is UpdateUserModelListener && context is OnDrawerNavigationPressedListener){
            updateUserModelListener = context
            onDrawerNavigationPressedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        updateUserModelListener = null
        onDrawerNavigationPressedListener = null
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentAlma", binding.profileAlmaMater.selectedItem.toString())
        outState.putString("currentEmail", binding.profileEmailEdt.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val currentAlma = savedInstanceState?.getString("currentAlma")
        val currentEmail = savedInstanceState?.getString("currentEmail")
        if(currentEmail != null){
            binding.profileEmailEdt.setText(currentEmail)
        }
        if(currentAlma != null){
            binding.profileAlmaMater.setSelection(schoolList.indexOf(currentAlma))
        }
    }

    private fun getUserVM(){
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            getUserVM()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
//        Picasso.get().load(Uri.parse(userViewModel.user?.profileImage)).into(binding.profileImage)
        schoolList = ArrayList()
        schoolList.addAll(Constant.schollList)

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.drop_down_item, schoolList)
        binding.profileAlmaMater.adapter = adapter
        binding.profileEmailEdt.setText(userViewModel.user?.username)
        binding.profileAlmaMater.setSelection(schoolList.indexOf(userViewModel.user?.almaMater))

        binding.pickImageButton.setOnClickListener{
            val pickingImage = Intent(Intent.ACTION_PICK)
            pickingImage.type = "image/*"
            pickingImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            startActivityForResult(pickingImage, PICK_IMAGE_INTENT)
        }

        binding.drawerNavigateButton.setOnClickListener{
            onDrawerNavigationPressedListener?.openDrawerFromFragment()
        }

        binding.saveProfileBtn.setOnClickListener {
            val username = binding.profileEmailEdt.text.toString()
            val almaMater = binding.profileAlmaMater.selectedItem.toString()
            if(username.isEmpty() || almaMater.isEmpty()){
                Toast.makeText(requireActivity(), R.string.please_fill, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.updateProfileProgress.visibility = View.VISIBLE
            binding.profileContent.visibility = View.INVISIBLE
            val token : String? = sharedPref.getString(getString(R.string.token_key), null)
            dataRepo.updateUser(username = username, almaMater = almaMater, id = userViewModel.user?.id!!, token = token!!).thenAcceptAsync{
                with(sharedPref.edit()){
                    val newUserJson = Gson().toJson(it.user)
                    with(sharedPref.edit()){
                        putString(getString(R.string.user_data_key), newUserJson)
                        apply()
                    }
                    updateUserModelListener?.updateUserModel(it.user)
                }
                requireActivity().runOnUiThread{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }.exceptionally{
                e -> requireActivity().runOnUiThread{
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
                null
            }.whenCompleteAsync{
                _,_ ->
                requireActivity().runOnUiThread {
                    binding.profileContent.visibility = View.VISIBLE
                    binding.updateProfileProgress.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_INTENT && resultCode == Activity.RESULT_OK && data!= null){
            val selectedImageUri = data.data!!
            binding.profileImage.setImageURI(selectedImageUri)
            val file = File(context?.cacheDir, "uploaded_image.jpg")
            val token : String? = sharedPref.getString(getString(R.string.token_key), null)
            binding.updateProfileProgress.visibility = View.VISIBLE
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", "file.jpg", requestBody)
            dataRepo.uploadImage(image = multipartBody, id = userViewModel.user?.id!!, token = token!!).thenAcceptAsync {
                with(sharedPref.edit()) {
                    putString(getString(R.string.user_data_key), Gson().toJson(it.user))
                    apply()
                }
                updateUserModelListener?.updateUserModel(it.user)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.updateProfileProgress.visibility = View.GONE
                }
            }.exceptionally {
                    e -> requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                binding.updateProfileProgress.visibility = View.GONE
            }
                null
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}