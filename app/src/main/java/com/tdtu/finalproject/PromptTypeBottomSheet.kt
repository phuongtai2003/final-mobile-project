package com.tdtu.finalproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.PromptTypeBottomSheetBinding
import com.tdtu.finalproject.utils.PromptOptionsListener

class PromptTypeBottomSheet : BottomSheetDialogFragment(){
    private lateinit var binding: PromptTypeBottomSheetBinding
    private var promptOptionsListener: PromptOptionsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is PromptOptionsListener){
            promptOptionsListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        promptOptionsListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PromptTypeBottomSheetBinding.inflate(inflater, container, false)
        arguments?.getBoolean("answerByDefinition")?.let {
            binding.answerByDefinitionSwitch.isChecked = it
        }
        arguments?.getBoolean("answerByVocabulary")?.let {
            binding.answerByVocabularySwitch.isChecked = it
        }
        arguments?.getBoolean("questionByDefinition")?.let {
            binding.promptByDefinitionSwitch.isChecked = it
        }
        arguments?.getBoolean("questionByVocabulary")?.let {
            binding.promptByVocabularySwitch.isChecked = it
        }
        binding.acceptBtn.setOnClickListener {
            promptOptionsListener?.onChoosingOption(binding.answerByDefinitionSwitch.isChecked, binding.answerByVocabularySwitch.isChecked, binding.promptByDefinitionSwitch.isChecked, binding.promptByVocabularySwitch.isChecked)
            dismiss()
        }
        return binding.root
    }
}