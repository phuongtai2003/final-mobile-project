package com.tdtu.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.databinding.FragmentProfileBinding
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.viewmodel.UserViewModel

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
    private lateinit var sharedPref : SharedPreferences
    private val binding get() = _binding!!


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        Picasso.get().load(Uri.parse("https://www.gravatar.com/avatar/5b6a331b0acabb80b4eb156da27b500a?s=100&r=x&d=retro")).into(binding.profileImage)
        schoolList = ArrayList();
        schoolList.add("TDTU")
        schoolList.add("HCMUS")
        schoolList.add("BKU")

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.drop_down_item, schoolList)
        binding.profileAlmaMater.adapter = adapter
        binding.profileEmailEdt.setText(userViewModel.user?.email)
        binding.profileAlmaMater.setSelection(schoolList.indexOf(userViewModel.user?.almaMater))

        binding.saveProfileBtn.setOnClickListener {
            val email = binding.profileEmailEdt.text.toString()
            val almaMater = binding.profileAlmaMater.selectedItem.toString()
            if(email.isEmpty() || almaMater.isEmpty()){
                Toast.makeText(requireContext(), R.string.please_fill, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val token : String? = sharedPref.getString(getString(R.string.token_key), null)
            Log.e("USER TAG", "onCreateView: $token")
            Log.e("USER TAG", "onCreateView: ${userViewModel.user?.id}")
            dataRepo.updateUser(email = email, almaMater = almaMater, id = userViewModel.user?.id!!, token = token!!).thenAcceptAsync{
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }.exceptionally{
                e -> Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                null
            }
        }
        return binding.root
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