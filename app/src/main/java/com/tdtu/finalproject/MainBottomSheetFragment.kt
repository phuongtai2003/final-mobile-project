package com.tdtu.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.MainBottomSheetBinding

class MainBottomSheetFragment: BottomSheetDialogFragment(){
    private lateinit var binding: MainBottomSheetBinding

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
        return binding.root
    }
}