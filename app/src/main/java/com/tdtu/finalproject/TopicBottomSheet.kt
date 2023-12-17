package com.tdtu.finalproject

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdtu.finalproject.databinding.TopicBottomSheetBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.utils.OnTopicDialogListener

class TopicBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: TopicBottomSheetBinding
    private var isYourTopic = false
    private lateinit var topic: Topic
    private var onTopicDialogListener: OnTopicDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnTopicDialogListener)
        {
            onTopicDialogListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onTopicDialogListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TopicBottomSheetBinding.inflate(inflater, container, false)
        val data = arguments?.getParcelable<Topic>("topic")
        isYourTopic = arguments?.getBoolean("isYourTopic")!!
        if(data == null)
        {
            dismiss()
        }
        else
        {
            topic = data
        }
        binding.addTopicToFolderBtn.setOnClickListener {
            onTopicDialogListener?.onSaveToFolder()
            dismiss()
        }
        binding.deleteTopicBtn.setOnClickListener {
            onTopicDialogListener?.onDeleteTopic()
            dismiss()
        }
        binding.editTopicBtn.setOnClickListener {
            onTopicDialogListener?.onEditTopic()
            dismiss()
        }
        binding.exportTopicToCsvBtn.setOnClickListener {
            onTopicDialogListener?.exportTopic()
            dismiss()
        }
        binding.saveAndEditTopicBtn.setOnClickListener {
            val intent = Intent(context, AddTopicActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("isEdit", false)
            startActivity(intent)
            dismiss()
        }
        if(!isYourTopic){
            binding.editTopicBtn.visibility = View.GONE
            binding.deleteTopicBtn.visibility = View.GONE
        }
        else{
            binding.editTopicBtn.visibility = View.VISIBLE
            binding.deleteTopicBtn.visibility = View.VISIBLE
        }
        return binding.root
    }
}