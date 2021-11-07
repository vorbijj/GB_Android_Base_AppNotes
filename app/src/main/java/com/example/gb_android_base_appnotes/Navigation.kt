package com.example.gb_android_base_appnotes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class Navigation(private val fragmentManager: FragmentManager) {
    fun addFragment(fragment: Fragment?, useBackStack: Boolean) {
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment)
        }
        fragmentTransaction.add(R.id.fragment_container, fragment!!)
        if (useBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    fun addFragmentSecondary(fragment: Fragment?) {
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(currentFragment!!)
        fragmentTransaction.add(R.id.fragment_container, fragment!!)
        val count = fragmentManager.backStackEntryCount
        if (count == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    fun toBackMainFragment() {
        val fragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}