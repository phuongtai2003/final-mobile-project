package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.FolderAdapter
import com.tdtu.finalproject.databinding.FragmentFolderBinding
import com.tdtu.finalproject.databinding.FragmentTopicBinding
import com.tdtu.finalproject.model.folder.CreateFolderRequest
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.CustomOnItemClickListener
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.HomeDataViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FolderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FolderFragment : Fragment(), OnDialogConfirmListener, CustomOnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentFolderBinding? = null
    private lateinit var homeDataViewModel: HomeDataViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var folderAdapter: FolderAdapter

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun initVM(){
        dataRepository = DataRepository.getInstance()
        homeDataViewModel = ViewModelProvider(requireActivity())[HomeDataViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(requireActivity().getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        homeDataViewModel.getFolderList()?.observe(requireActivity()) {
            val mutableList = it.toMutableList()
            if(mutableList.isEmpty()){
                binding.folderRecyclerView.visibility = View.GONE
                binding.noFolderLayout.visibility = View.VISIBLE
            }
            else{
                binding.folderRecyclerView.visibility = View.VISIBLE
                binding.noFolderLayout.visibility = View.GONE
                folderAdapter = FolderAdapter(requireActivity(), mutableList, R.layout.folder_library_item, homeDataViewModel.getUser()?.value!!, this)
                binding.folderRecyclerView.setHasFixedSize(true)
                binding.folderRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.folderRecyclerView.adapter = folderAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        initVM()
        binding.addTopicBtn.setOnClickListener {
            Utils.showCreateFolderDialog(
                Gravity.CENTER,
                requireActivity(),
                this,
            )
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FolderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateFolderDialogConfirm(
        folderNameEnglish: String,
        folderNameVietnamese: String
    ) {
        dataRepository.createFolder(folderNameEnglish, folderNameVietnamese, sharedPreferences.getString(requireActivity().getString(R.string.token_key), null)!!).thenAccept {
            Utils.showDialog(Gravity.CENTER, requireActivity().getString(R.string.create_folder_success), requireActivity())
            dataRepository.getFolderByUser(homeDataViewModel.getUser()?.value!!.id, sharedPreferences.getString(requireActivity().getString(R.string.token_key), null)!!).thenAccept {
                homeDataViewModel.setFolderList(it)
            }.exceptionally{
                Utils.showDialog(Gravity.CENTER, it.message.toString(), requireActivity())
                null
            }
        }.exceptionally {
            Utils.showDialog(Gravity.CENTER, it.message.toString(), requireActivity())
            null
        }
    }

    override fun onAddTopicToFolderDialogConfirm() {

    }

    override fun onDeleteFolderDialogConfirm() {

    }

    override fun onUpgradePremiumDialogConfirm() {
        TODO("Not yet implemented")
    }

    override fun onTopicClick(topic: Topic) {
    }

    override fun onFolderClick(folder: Folder) {
        val intent = Intent(requireActivity(), FolderDetailActivity::class.java)
        intent.putExtra("folder", folder)
        startActivity(intent)
    }
}