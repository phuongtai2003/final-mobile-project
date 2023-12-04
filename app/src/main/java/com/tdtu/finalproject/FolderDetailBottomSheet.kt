package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.FolderDetailBottomSheetBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils

class FolderDetailBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: FolderDetailBottomSheetBinding
    private lateinit var folder: Folder
    private var onDialogConfirmListener : OnDialogConfirmListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnDialogConfirmListener){
            onDialogConfirmListener = context;
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
        binding = FolderDetailBottomSheetBinding.inflate(layoutInflater, container, false)
        folder = this.requireArguments().getParcelable<Folder>("folder")!!
        binding.editFolderOptionBtn.setOnClickListener {
            Utils.showEditFolderDialog(folder.folderNameEnglish!!, folder.folderNameVietnamese!!, Gravity.CENTER, requireActivity(), onDialogConfirmListener!!)
            dismiss()
        }
        binding.addTopicOptionBtn.setOnClickListener {
            onDialogConfirmListener?.onAddTopicToFolderDialogConfirm()
            dismiss()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.removeFolderOptionBtn.setOnClickListener {
            Utils.showDeleteFolderConfirmDialog(Gravity.CENTER, getString(R.string.remove_folder_confirm_message), requireActivity(), onDialogConfirmListener!!)
            dismiss()
        }
        return binding.root
    }
}