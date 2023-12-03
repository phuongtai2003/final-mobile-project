package com.tdtu.finalproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tdtu.finalproject.adapter.TopicAdapter
import com.tdtu.finalproject.databinding.FragmentHomePageBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.utils.OnBottomNavigationChangeListener
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener
import com.tdtu.finalproject.viewmodel.HomeDataViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHomePageBinding? = null
    private var onBottomNavigationChangeListener: OnBottomNavigationChangeListener? = null
    private var onDrawerNavigationPressedListener: OnDrawerNavigationPressedListener? = null
    private lateinit var userViewModel :HomeDataViewModel
    private lateinit var topicAdapter: TopicAdapter

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBottomNavigationChangeListener && context is OnDrawerNavigationPressedListener) {
            onBottomNavigationChangeListener = context
            onDrawerNavigationPressedListener = context
        } else {
            throw RuntimeException("$context must implement OnBottomNavigationChangeListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onBottomNavigationChangeListener = null
        onDrawerNavigationPressedListener = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            getUserVM()
        }
    }

    private fun getUserVM(){
        userViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
        userViewModel.getUser()?.observe(viewLifecycleOwner) {
            binding.helloText.text = "${getString(R.string.hello)}, ${it.username}"
        }
        userViewModel.getTopicsList()?.observe(viewLifecycleOwner){
            val mutableList: MutableList<Topic> = it.toMutableList()
            if(mutableList.isEmpty()){
                binding.topicRecyclerView.visibility = View.GONE
                binding.noTopicText.visibility = View.VISIBLE
            } else{
                binding.topicRecyclerView.visibility = View.VISIBLE
                binding.noTopicText.visibility = View.GONE
            }
            topicAdapter = TopicAdapter(requireContext(), mutableList, R.layout.topic_menu_item, userViewModel.getUser()?.value!!)
            binding.topicRecyclerView.adapter = topicAdapter
            binding.topicRecyclerView.clipToPadding = false
            binding.topicRecyclerView.clipChildren = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        getUserVM()
        binding.openDrawerBtn.setOnClickListener {
            onDrawerNavigationPressedListener?.openDrawerFromFragment()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}