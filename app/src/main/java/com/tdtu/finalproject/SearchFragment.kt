package com.tdtu.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.TopicAdapter
import com.tdtu.finalproject.databinding.FragmentSearchBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.utils.CustomOnItemClickListener
import com.tdtu.finalproject.viewmodel.HomeDataViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), CustomOnItemClickListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var searchFragmentBinding: FragmentSearchBinding? = null
    private lateinit var homeDataViewModel: HomeDataViewModel
    private var topicAdapter: TopicAdapter? = null
    private lateinit var originalTopicsList: List<Topic>
    private val binding get() = searchFragmentBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun initVM(){
        originalTopicsList = ArrayList()
        homeDataViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        binding.searchBar.text.clear()
        binding.searchBar.clearFocus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        searchFragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        initVM()
        homeDataViewModel.getPublicTopicsList().observe(viewLifecycleOwner){topicList->
            originalTopicsList = topicList
            val mutableList = topicList.toMutableList()
            if(topicAdapter == null || topicAdapter?.itemCount == 0){
                topicAdapter = TopicAdapter(requireActivity(), mutableList, R.layout.topic_library_item, this)
                binding.publicTopicRecyclerView.setHasFixedSize(true)
                binding.publicTopicRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.publicTopicRecyclerView.adapter = topicAdapter
            }
            else{
                topicAdapter?.setTopics(mutableList)
            }
        }
        binding.searchBar.addTextChangedListener {
            val mutableList = originalTopicsList.toMutableList()
            val searchText = binding.searchBar.text.toString()
            if(searchText.isNotEmpty()){
                val filteredList = mutableList.filter { topic -> topic.topicNameEnglish!!.contains(searchText, true) || topic.topicNameVietnamese!!.contains(searchText, true) }
                topicAdapter?.setTopics(filteredList.toMutableList())
            }
            else{
                topicAdapter?.setTopics(mutableList)
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
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onTopicClick(topic: Topic) {
        val intent = Intent(requireContext(), TopicActivity::class.java)
        intent.putExtra("topic", topic)
        startActivity(intent)
    }

    override fun onFolderClick(folder: Folder) {
    }
}