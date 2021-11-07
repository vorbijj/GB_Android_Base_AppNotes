package com.example.gb_android_base_appnotes.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gb_android_base_appnotes.MainActivity
import com.example.gb_android_base_appnotes.R
import com.example.gb_android_base_appnotes.data.CardNote
import com.example.gb_android_base_appnotes.data.CardsSource
import com.example.gb_android_base_appnotes.data.CardsSourceFirebaseImpl
import com.example.gb_android_base_appnotes.data.CardsSourceResponse
import com.example.gb_android_base_appnotes.observe.Observer
import com.example.gb_android_base_appnotes.observe.Publisher
import com.example.gb_android_base_appnotes.ui.CardFragment.Companion.newInstance

class TitleFragment : Fragment() {
    private var currentCardNote: CardNote? = null
    private var isLandscape = false
    private var data: CardsSource? = null
    private var adapter: NoteAdapter? = null
    private var publisherTitle: Publisher? = null
    private var activity: MainActivity? = null
    private var moveToFirstPosition = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_title, container, false)
        setHasOptionsMenu(true)
        initRecyclerView(view)
        data = CardsSourceFirebaseImpl().init(object : CardsSourceResponse {
            override fun initialized(cardsNote: CardsSource?) {
                adapter!!.notifyDataSetChanged()
            }
        })
        adapter!!.setDataSource(data)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_lines)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = NoteAdapter(this)
        recyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(context,
                LinearLayoutManager.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.separator))
        recyclerView.addItemDecoration(itemDecoration)
        val animator = DefaultItemAnimator()
        animator.addDuration = MY_DEFAULT_DURATION.toLong()
        animator.removeDuration = MY_DEFAULT_DURATION.toLong()
        recyclerView.itemAnimator = animator
        if (moveToFirstPosition && data!!.size() > 0) {
            recyclerView.scrollToPosition(0)
            moveToFirstPosition = false
        }
        adapter!!.SetOnItemClickListener { view, position ->
            currentCardNote = CardNote(data!!.getNoteData(position)!!.title,
                    data!!.getNoteData(position)!!.date,
                    data!!.getNoteData(position)!!.description,
                    data!!.getNoteData(position)!!.isLike)
            showNote(currentCardNote!!)
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
        publisherTitle = activity!!.publisher
        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onDetach() {
        activity = null
        publisherTitle = null
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_cards, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onItemSelected(item.itemId) || super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CURRENT_NOTE, currentCardNote)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            currentCardNote = savedInstanceState.getParcelable(CURRENT_NOTE)
        }
        if (isLandscape) {
            showLandNote(currentCardNote)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.card_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return onItemSelected(item.itemId) || super.onContextItemSelected(item)
    }

    private fun showNote(currentCardNote: CardNote) {
        if (isLandscape) {
            showLandNote(currentCardNote)
        } else {
            showPortNote(currentCardNote)
        }
    }

    private fun showLandNote(currentCardNote: CardNote?) {
        val detail = NoteFragment.newInstance(currentCardNote)
        val fragmentManager = requireActivity()
                .supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_note, detail)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }

    private fun showPortNote(currentCardNote: CardNote) {
        replaceFragment(currentCardNote)
    }

    private fun showEmptyNote() {
        val fragmentManager = requireActivity()
                .supportFragmentManager
        val detail = fragmentManager.findFragmentById(R.id.fragment_note)
        if (detail != null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(detail)
            fragmentTransaction.commit()
        }
    }

    private fun onItemSelected(menuItem: Int): Boolean {
        when (menuItem) {
            R.id.action_add -> {
                addFragment(CardFragment.newInstance(), true)
                publisherTitle!!.subscribe(object : Observer {
                    override fun updateCardNote(cardNote: CardNote?) {
                        data!!.addCardNote(cardNote)
                        adapter!!.notifyItemInserted(data!!.size() - 1)
                        moveToFirstPosition = true
                    }
                })
                return true
            }
            R.id.action_update -> {
                val updatePosition = adapter!!.menuPosition
                addFragment(newInstance(data!!.getNoteData(updatePosition)), true)
                publisherTitle!!.subscribe(object : Observer {
                    override fun updateCardNote(cardNote: CardNote?) {
                        data!!.updateCardNote(updatePosition, cardNote)
                        adapter!!.notifyItemChanged(updatePosition)
                    }
                })
                showEmptyNote()
                return true
            }
            R.id.action_delete -> {
                val deletePosition = adapter!!.menuPosition
                openDeleteDialog(deletePosition)
                return true
            }
            R.id.action_clear -> {
                openClearDialog()
                return true
            }
        }
        return false
    }

    private fun openDeleteDialog(deletePosition: Int) {
        val deleteDialog = AlertDialog.Builder(activity)
        deleteDialog.setTitle(R.string.exclamation)
                .setMessage(R.string.delete_question)
                .setIcon(R.drawable.ic_baseline_alert)
                .setCancelable(false)
                .setNegativeButton(R.string.text_no) { dialog, id -> Toast.makeText(activity, R.string.text_no_, Toast.LENGTH_SHORT).show() }
                .setPositiveButton(R.string.text_yes) { dialog, id ->
                    Toast.makeText(activity, R.string.text_yes_, Toast.LENGTH_SHORT).show()
                    data!!.deleteCardNote(deletePosition)
                    adapter!!.notifyItemRemoved(deletePosition)
                    showEmptyNote()
                }
        val alert = deleteDialog.create()
        alert.show()
    }

    private fun openClearDialog() {
        val clearDialog = AlertDialog.Builder(activity)
        clearDialog.setTitle(R.string.exclamation)
                .setMessage(R.string.clear_question)
                .setIcon(R.drawable.ic_baseline_alert)
                .setCancelable(false)
                .setNegativeButton(R.string.text_no) { dialog, id -> Toast.makeText(activity, R.string.text_no_, Toast.LENGTH_SHORT).show() }
                .setPositiveButton(R.string.text_yes) { dialog, id ->
                    Toast.makeText(activity, R.string.text_yes_, Toast.LENGTH_SHORT).show()
                    data!!.clearCardNote()
                    adapter!!.notifyDataSetChanged()
                    showEmptyNote()
                }
        val alert = clearDialog.create()
        alert.show()
    }

    private fun addFragment(fragment: Fragment, useBackStack: Boolean) {
        val fragmentManager = requireActivity().supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment)
        }
        fragmentTransaction.add(R.id.fragment_container, fragment)
        if (useBackStack) {
            val count = fragmentManager.backStackEntryCount
            if (count == 0) {
                fragmentTransaction.addToBackStack(null)
            }
        }
        fragmentTransaction.commit()
    }

    private fun replaceFragment(currentCardNote: CardNote) {
        val fragmentManager = requireActivity().supportFragmentManager
        val detail = NoteFragment.newInstance(currentCardNote)
        val fragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        fragmentTransaction.remove(currentFragment!!)
        fragmentTransaction.add(R.id.fragment_container, detail)
        val count = fragmentManager.backStackEntryCount
        if (count == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    companion object {
        private const val MY_DEFAULT_DURATION = 1000
        const val CURRENT_NOTE = "CurrentNote"
        fun newInstance(): TitleFragment {
            return TitleFragment()
        }
    }
}