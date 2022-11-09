package com.example.galon.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.dynamic.SupportFragmentWrapper

class ViewPagerAdaper(supportFragmentManager: FragmentManager) :
    FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val FragmentList = ArrayList<Fragment>()
    private val FragmentTitleList = ArrayList<String>()

    override fun getCount(): Int {
        return FragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return FragmentList[position]
    }

    private fun getPagerTitle(position: Int): CharSequence?{
        return FragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String){
        FragmentList.add(fragment)
        FragmentTitleList.add(title)
    }

}