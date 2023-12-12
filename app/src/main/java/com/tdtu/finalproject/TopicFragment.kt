package com.tdtu.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.TopicAdapter
import com.tdtu.finalproject.databinding.FragmentTopicBinding
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
 * Use the [TopicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TopicFragment : Fragment(), CustomOnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentTopicBinding? = null
    private lateinit var homeDataViewModel: HomeDataViewModel
    private lateinit var topicAdapter: TopicAdapter

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun getVM() {
        homeDataViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
        homeDataViewModel.getTopicsList().observe(viewLifecycleOwner) {
            val mutableList = it.filter { topic -> topic.ownerId?.id == homeDataViewModel.getUser().value?.id }.toMutableList()
            if(mutableList.isEmpty()){
                binding.noTopicLayout.visibility = View.VISIBLE
                binding.topicRecyclerView.visibility = View.GONE
                binding.addTopicBtn.setOnClickListener {
                    val addTopicIntent = Intent(requireActivity(), AddTopicActivity::class.java)
                    startActivity(addTopicIntent)
                }
            }
            else{
                binding.noTopicLayout.visibility = View.GONE
                binding.topicRecyclerView.visibility = View.VISIBLE
                topicAdapter = TopicAdapter(requireActivity(), mutableList, R.layout.topic_library_item, this)
                binding.topicRecyclerView.setHasFixedSize(true)
                binding.topicRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
               binding.topicRecyclerView.adapter = topicAdapter
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopicBinding.inflate(inflater, container, false)
        getVM()
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TopicFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onTopicClick(topic: Topic) {
        val intent = Intent(requireActivity(), TopicActivity::class.java)
        intent.putExtra("topic", topic)
        startActivity(intent)
    }

    override fun onFolderClick(folder: Folder) {
    }
}