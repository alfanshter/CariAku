package com.alfanshter.aplikasiiska.home.ui.MenuUpload.foto

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                FotoFragment()
            }
            else->
                GalleryFragment()
        }

    }

    override fun getCount(): Int {
       return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0->"Gallery"
            else ->"Kamera"
        }
    }

}