package com.tdtu.finalproject

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.constants.Constant
import com.tdtu.finalproject.databinding.FragmentProfileBinding
import com.tdtu.finalproject.model.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener
import com.tdtu.finalproject.utils.UpdateUserModelListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.UserViewModel
import java.io.File
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
    private val UPDATE_USER_REQUEST : Int = 555
    private var updateUserModelListener : UpdateUserModelListener? = null
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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

        Picasso.get().load(Uri.parse(userViewModel.user?.profileImage)).into(binding.profileImage)
        binding.profileUsername.text = userViewModel.user?.username

        binding.drawerNavigateButton.setOnClickListener{
            onDrawerNavigationPressedListener?.openDrawerFromFragment()
        }

        binding.myAccountBtn.setOnClickListener {
            val goToAccount = Intent(requireActivity(), AccountActivity::class.java)
            goToAccount.putExtra(getString(R.string.user_data_key), userViewModel.user)
            startActivityForResult(goToAccount, UPDATE_USER_REQUEST)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == UPDATE_USER_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            val temp : User? = data.getParcelableExtra(getString(R.string.user_data_key))
            if(temp != null){
                updateUserModelListener?.updateUserModel(temp)
                binding.profileUsername.text = userViewModel.user?.username
                Picasso.get().load(Uri.parse(userViewModel.user?.profileImage)).into(binding.profileImage)
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