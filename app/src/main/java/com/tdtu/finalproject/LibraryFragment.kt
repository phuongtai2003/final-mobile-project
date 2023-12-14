package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tdtu.finalproject.adapter.LibraryScreenTabAdapter
import com.tdtu.finalproject.databinding.FragmentLibraryBinding
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.HomeDataViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentLibraryBinding? = null
    private lateinit var libraryScreenTabAdapter: LibraryScreenTabAdapter
    private lateinit var tabLayout: TabLayout
    private var onDialogConfirmListener: OnDialogConfirmListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDialogConfirmListener) {
            onDialogConfirmListener = context
        } else {
            throw RuntimeException("$context must implement OnDialogConfirmListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onDialogConfirmListener = null
    }

    private val binding get() = _binding!!

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
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        libraryScreenTabAdapter = LibraryScreenTabAdapter(requireActivity())
        binding.pagesLayoutList.adapter = libraryScreenTabAdapter
        tabLayout = binding.tabLayoutList
        TabLayoutMediator(tabLayout, binding.pagesLayoutList) { tab, position ->
            when (position) {
                0 ->{
                    tab.text = getString(R.string.topic)
                }
                1 ->{
                    tab.text = getString(R.string.folder)
                }
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 ->{
                        binding.addBtn.setOnClickListener{
                            val addTopicIntent = Intent(requireActivity(), AddTopicActivity::class.java)
                            startActivity(addTopicIntent)
                        }
                    }
                    1 ->{
                        binding.addBtn.setOnClickListener{
                            Utils.showCreateFolderDialog(
                                Gravity.CENTER,
                                requireActivity(),
                                onDialogConfirmListener!!
                            )
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 ->{
                        binding.addBtn.setOnClickListener{
                            val addTopicIntent = Intent(requireActivity(), AddTopicActivity::class.java)
                            startActivity(addTopicIntent)
                        }
                    }
                    1 ->{
                        binding.addBtn.setOnClickListener{
                            Utils.showCreateFolderDialog(
                                Gravity.CENTER,
                                requireActivity(),
                                onDialogConfirmListener!!
                            )
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 ->{
                        binding.addBtn.setOnClickListener{
                            val addTopicIntent = Intent(requireActivity(), AddTopicActivity::class.java)
                            startActivity(addTopicIntent)
                        }
                    }
                    1 ->{
                        binding.addBtn.setOnClickListener{
                            Utils.showCreateFolderDialog(
                                Gravity.CENTER,
                                requireActivity(),
                                onDialogConfirmListener!!
                            )
                        }
                    }
                }
            }
        })
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            val args = arguments
            if(args != null){
                val tabIndex = args.getInt("tabIndex")
                binding.pagesLayoutList.setCurrentItem(tabIndex, true)
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
         * @return A new instance of fragment LibraryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}