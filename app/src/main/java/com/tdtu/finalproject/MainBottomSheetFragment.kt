package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.MainBottomSheetBinding
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils

class MainBottomSheetFragment: BottomSheetDialogFragment(){
    private lateinit var binding: MainBottomSheetBinding
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainBottomSheetBinding.inflate(inflater, container, false)
        binding.addTopicBtn.setOnClickListener { view ->
            val intent = Intent(view.context, AddTopicActivity::class.java)
            startActivity(intent)
            dismiss()
        }
        binding.addFolderBtn.setOnClickListener {
            Utils.showCreateFolderDialog(
                Gravity.CENTER,
                requireActivity(),
                onDialogConfirmListener!!
            )
            dismiss()
        }
        return binding.root
    }
}