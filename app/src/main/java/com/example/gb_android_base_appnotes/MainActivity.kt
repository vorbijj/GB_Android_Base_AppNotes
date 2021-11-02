package com.example.gb_android_base_appnotes

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gb_android_base_appnotes.observe.Publisher
import com.example.gb_android_base_appnotes.ui.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navigation: Navigation
    val publisher = Publisher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation = Navigation(supportFragmentManager)
        val toolbar = initToolbar()
        initDrawer(toolbar)
        navigation.addFragment(TitleFragment.newInstance(), false)
    }

    private fun initDrawer(toolbar: Toolbar) {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.action_main -> {
                    navigation.toBackMainFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_settings -> {
                    navigation.addFragmentSecondary(SettingsFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_about -> {
                    navigation.addFragmentSecondary(AboutFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun initToolbar(): Toolbar {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        return toolbar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_sort -> {
                navigation.addFragmentSecondary(SortFragment())
                return true
            }
            R.id.action_favorite -> {
                navigation.addFragmentSecondary(FavoriteFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.action_search)
        val searchText = search.actionView as SearchView
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(this@MainActivity, query,
                        Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (supportFragmentManager.findFragmentById(R.id.fragment_container)!!.javaClass.canonicalName
                    == TitleFragment().javaClass.canonicalName) {
                openQuitDialog()
            } else {
                removeCurrentFragment()
                supportFragmentManager.popBackStack()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun removeCurrentFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val currentFrag = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val noteFrag = supportFragmentManager.findFragmentById(R.id.fragment_note)
        if (currentFrag != null) {
            transaction.remove(currentFrag)
            if (noteFrag != null) {
                transaction.remove(noteFrag)
            }
        }
        transaction.commit()
    }

    private fun openQuitDialog() {
        val quitDialog = AlertDialog.Builder(this@MainActivity)
        quitDialog.setTitle(R.string.question_exit)
        quitDialog.setPositiveButton(R.string.text_yes) { dialog, which -> finish() }
        quitDialog.setNegativeButton(R.string.text_no) { dialog, which -> }
        quitDialog.show()
    }
}