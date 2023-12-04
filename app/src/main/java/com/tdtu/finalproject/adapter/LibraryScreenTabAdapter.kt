package com.tdtu.finalproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tdtu.finalproject.FolderFragment
import com.tdtu.finalproject.HomeActivity
import com.tdtu.finalproject.TopicFragment

class LibraryScreenTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TopicFragment()
            1 -> FolderFragment()
            else -> TopicFragment()
        }
    }

}