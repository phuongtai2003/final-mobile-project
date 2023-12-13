package com.tdtu.finalproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.databinding.FragmentProfileBinding
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener
import com.tdtu.finalproject.viewmodel.HomeDataViewModel


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
    private lateinit var homeDataViewModel : HomeDataViewModel
    private var _binding : FragmentProfileBinding? = null
    private val UPDATE_USER_REQUEST : Int = 555
    private var onDrawerNavigationPressedListener: OnDrawerNavigationPressedListener? = null
    private lateinit var sharedPref : SharedPreferences
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnDrawerNavigationPressedListener){
            onDrawerNavigationPressedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
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
        homeDataViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
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

        homeDataViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        homeDataViewModel.getUser().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.profileUsername.text = it.username
                Picasso.get().load(Uri.parse(it.profileImage)).into(binding.profileImage)
            }
        }

        binding.myAccountBtn.setOnClickListener {
            val goToAccount = Intent(requireActivity(), AccountActivity::class.java)
            goToAccount.putExtra(getString(R.string.user_data_key), homeDataViewModel.getUser()?.value)
            startActivityForResult(goToAccount, UPDATE_USER_REQUEST)
        }

        binding.logoutBtn.setOnClickListener {
            with(sharedPref.edit()){
                remove(getString(R.string.user_data_key))
                remove(getString(R.string.token_key))
                apply()
            }
            val intro = Intent(activity, MainActivity::class.java)
            intro.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intro)
        }

        binding.openDrawerBtn.setOnClickListener {
            onDrawerNavigationPressedListener?.openDrawerFromFragment()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == UPDATE_USER_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            val temp : User? = data.getParcelableExtra(getString(R.string.user_data_key))
            if(temp != null){
                homeDataViewModel.setUser(temp)
                binding.profileUsername.text = homeDataViewModel.getUser().value?.username
                sharedPref.edit().putString(getString(R.string.user_data_key), Gson().toJson(homeDataViewModel.getUser().value)).apply()
                Picasso.get().load(Uri.parse(homeDataViewModel.getUser().value?.profileImage)).into(binding.profileImage)
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