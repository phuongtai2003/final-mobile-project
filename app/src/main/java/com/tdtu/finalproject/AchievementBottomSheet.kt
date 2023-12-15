package com.tdtu.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.databinding.AchievementBottomSheetBinding
import com.tdtu.finalproject.model.achievement.Achievement

class AchievementBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: AchievementBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AchievementBottomSheetBinding.inflate(inflater, container, false)
        arguments?.let {
            bundle ->
            bundle.getParcelable<Achievement>("achievement")?.let {
                achievement ->
                Picasso.get().load(achievement.image).into(binding.achievementImageView)
                binding.achievementTitleTextView.text= achievement.name
                binding.achievementDescriptionTextView.text = achievement.description
            }
        }
        binding.understandButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}