package com.example.gb_android_base_appnotes.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.example.gb_android_base_appnotes.MainActivity
import com.example.gb_android_base_appnotes.R
import com.example.gb_android_base_appnotes.data.CardNote
import com.example.gb_android_base_appnotes.observe.Publisher
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CardFragment : Fragment() {
    private var cardNote: CardNote? = null
    private var publisher: Publisher? = null
    private var title: TextInputEditText? = null
    private var description: TextInputEditText? = null
    private var datePicker: DatePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            cardNote = requireArguments().getParcelable(ARG_CARD_NOTE)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as MainActivity
        publisher = activity.publisher
    }

    override fun onDetach() {
        publisher = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_card, container, false)
        setHasOptionsMenu(true)
        initView(view)
        if (cardNote != null) {
            populateView()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_back, menu)
        menu.removeItem(R.id.action_search)
        menu.removeItem(R.id.action_favorite)
        menu.removeItem(R.id.action_sort)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_back -> {
                val fm = requireActivity().supportFragmentManager
                fm.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        cardNote = collectCardNote()
    }

    override fun onDestroy() {
        super.onDestroy()
        publisher!!.notifySingle(cardNote)
    }

    private fun collectCardNote(): CardNote {
        val title = title!!.text.toString()
        val description = description!!.text.toString()
        val date = dateFromDatePicker
        val answer: CardNote
        val like: Boolean
        if (cardNote != null) {
            like = cardNote!!.isLike
            answer = CardNote(title, date, description, like)
            answer.id = cardNote!!.id
        } else {
            like = false
            answer = CardNote(title, date, description, like)
        }
        return answer
    }

    private val dateFromDatePicker: Date
        private get() {
            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = datePicker!!.year
            cal[Calendar.MONTH] = datePicker!!.month
            cal[Calendar.DAY_OF_MONTH] = datePicker!!.dayOfMonth
            return cal.time
        }

    private fun initView(view: View) {
        title = view.findViewById(R.id.inputTitle)
        description = view.findViewById(R.id.inputDescription)
        datePicker = view.findViewById(R.id.inputDate)
    }

    private fun populateView() {
        title!!.setText(cardNote!!.title)
        description!!.setText(cardNote!!.description)
        initDatePicker(cardNote!!.date)
    }

    private fun initDatePicker(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        datePicker!!.init(calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH],
                null)
    }

    companion object {
        private const val ARG_CARD_NOTE = "Param_CardNote"
        fun newInstance(cardNote: CardNote?): CardFragment {
            val fragment = CardFragment()
            val args = Bundle()
            args.putParcelable(ARG_CARD_NOTE, cardNote)
            fragment.arguments = args
            return fragment
        }

        @JvmStatic
        fun newInstance(): CardFragment {
            return CardFragment()
        }
    }
}