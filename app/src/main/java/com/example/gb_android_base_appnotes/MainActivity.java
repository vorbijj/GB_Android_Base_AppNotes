package com.example.gb_android_base_appnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ManageFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            initViewFirst();
        } else {
            initView();
        }
    }

    private void initViewFirst() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new TitleFragment());
        fragmentTransaction.commit();
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TitleFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void replaceFragment(Note currentNote) {
        NoteFragment detail = NoteFragment.newInstance(currentNote);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, detail);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}