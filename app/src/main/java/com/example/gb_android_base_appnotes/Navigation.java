package com.example.gb_android_base_appnotes;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.ui.NoteFragment;
import com.example.gb_android_base_appnotes.ui.TitleFragment;


public class Navigation {
    private final FragmentManager fragmentManager;

    public Navigation(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void addFragment(Fragment fragment, boolean useBackStack) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        fragmentTransaction.add(R.id.fragment_container, fragment);

        if (useBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

    public void addFragmentSecondary(Fragment fragment) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(currentFragment);
        fragmentTransaction.add(R.id.fragment_container, fragment);

        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public void toBackMainFragment() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
