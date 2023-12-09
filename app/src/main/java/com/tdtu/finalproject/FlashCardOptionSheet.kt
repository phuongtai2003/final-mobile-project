package com.tdtu.finalproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.FlashCardOptionsSheetBinding
import com.tdtu.finalproject.utils.FlashCardOptionsListener
import com.tdtu.finalproject.utils.Language

class FlashCardOptionSheet: BottomSheetDialogFragment() {
    private lateinit var binding: FlashCardOptionsSheetBinding
    private var onFlashCardOptionsListener: FlashCardOptionsListener? = null
    private var isShuffled = false
    private var isAutoPlayAudio = false
    private var isFrontFirst = false
    private var language = Language.ENGLISH
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FlashCardOptionsListener){
            onFlashCardOptionsListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onFlashCardOptionsListener = null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FlashCardOptionsSheetBinding.inflate(inflater, container, false)
        arguments?.let {
            isShuffled = it.getBoolean("isShuffled")
            isAutoPlayAudio = it.getBoolean("isAutoPlayAudio")
            isFrontFirst = it.getBoolean("isFrontFirst")
            language = it.getSerializable("language") as Language
        }
        if(isShuffled){
            binding.shuffleBtn.setColorFilter(resources.getColor(R.color.secondary_color), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        if(isAutoPlayAudio){
            binding.autoPlaySoundBtn.setColorFilter(resources.getColor(R.color.secondary_color), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        if(isFrontFirst){
            binding.definitionFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
        }else{
            binding.vocabularyFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
        }
        when(language){
            Language.ENGLISH -> {
                binding.englishFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
            }
            Language.VIETNAMESE -> {
                binding.vietnameseFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
            }
        }
        binding.shuffleBtn.setOnClickListener {
            isShuffled = !isShuffled
            if(isShuffled){
                binding.shuffleBtn.setColorFilter(resources.getColor(R.color.secondary_color), android.graphics.PorterDuff.Mode.SRC_IN)
            }else{
                binding.shuffleBtn.setColorFilter(resources.getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
        binding.autoPlaySoundBtn.setOnClickListener {
            isAutoPlayAudio = !isAutoPlayAudio
            if(isAutoPlayAudio){
                binding.autoPlaySoundBtn.setColorFilter(resources.getColor(R.color.secondary_color), android.graphics.PorterDuff.Mode.SRC_IN)
            }else{
                binding.autoPlaySoundBtn.setColorFilter(resources.getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
        binding.definitionFirstBtn.setOnClickListener {
            isFrontFirst = true
            binding.definitionFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
            binding.vocabularyFirstBtn.setTextColor(resources.getColor(R.color.white))
        }
        binding.vocabularyFirstBtn.setOnClickListener {
            isFrontFirst = false
            binding.definitionFirstBtn.setTextColor(resources.getColor(R.color.white))
            binding.vocabularyFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
        }
        binding.englishFirstBtn.setOnClickListener {
            language = Language.ENGLISH
            binding.englishFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
            binding.vietnameseFirstBtn.setTextColor(resources.getColor(R.color.white))
        }
        binding.vietnameseFirstBtn.setOnClickListener {
            language = Language.VIETNAMESE
            binding.englishFirstBtn.setTextColor(resources.getColor(R.color.white))
            binding.vietnameseFirstBtn.setTextColor(resources.getColor(R.color.secondary_color))
        }
        binding.applyBtn.setOnClickListener {
            onFlashCardOptionsListener?.onApply(isShuffled, isAutoPlayAudio, isFrontFirst, language)
            dismiss()
        }
        return binding.root
    }
}