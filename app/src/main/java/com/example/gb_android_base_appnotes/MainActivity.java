package com.example.gb_android_base_appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.ui.AboutFragment;
import com.example.gb_android_base_appnotes.ui.FavoriteFragment;
import com.example.gb_android_base_appnotes.ui.ManageFragment;
import com.example.gb_android_base_appnotes.ui.NoteFragment;
import com.example.gb_android_base_appnotes.ui.SettingsFragment;
import com.example.gb_android_base_appnotes.ui.SortFragment;
import com.example.gb_android_base_appnotes.ui.TitleFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements ManageFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = initToolbar();

        initDrawer(toolbar);

        initView();
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id){
                    case R.id.action_main:
                        toBackMainFragment();
                        return true;
                    case R.id.action_settings:
                        addFragment(new SettingsFragment());
                        return true;
                    case R.id.action_about:
                        addFragment(new AboutFragment());
                        return true;
                }
                return false;
            }
        });
    }

    private void toBackMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            if (fragment != null) {
                fm.beginTransaction().remove(fragment).commit();
            }
            fm.popBackStack();
        }
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }

        fragmentTransaction.add(R.id.fragment_container, new TitleFragment());
        fragmentTransaction.commit();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_sort:
                addFragment(new SortFragment());
                return true;
            case R.id.action_favorite:
                addFragment(new FavoriteFragment());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        String nameFragment = currentFragment.getClass().getCanonicalName();
        String nameFagmentTitle = new TitleFragment().getClass().getCanonicalName();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(currentFragment);
        fragmentTransaction.add(R.id.fragment_container, fragment);

        if (nameFragment.equals(nameFagmentTitle)) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void replaceFragment(CardNote currentCardNote) {
        NoteFragment detail = NoteFragment.newInstance(currentCardNote);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        fragmentTransaction.remove(currentFragment);

        fragmentTransaction.add(R.id.fragment_container, detail);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getCanonicalName()
                    .equals(new TitleFragment().getClass().getCanonicalName())) {
                openQuitDialog();
            }  else {
                getSupportFragmentManager().popBackStack();
                removeCurrentFragment();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFrag =  getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        String fragName = "NONE";

        if (currentFrag!=null) {
            fragName = currentFrag.getClass().getSimpleName();
        }

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle(R.string.question_exit);

        quitDialog.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        quitDialog.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }
}