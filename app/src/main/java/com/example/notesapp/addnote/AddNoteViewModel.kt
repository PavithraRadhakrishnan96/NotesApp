package com.example.notesapp.addnote

import android.app.DatePickerDialog
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.di.AppModule
import com.example.notesapp.di.DaggerViewModelInjector
import com.example.notesapp.di.ViewModelInjector
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.annotations.NonNull
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddNoteViewModel : ViewModel() {
    var authListener: AuthListener? = null
    var title: String? = null
    var date: String? = null
    var notes: String? = null
    var passedId: String? = null

    @Inject
    lateinit var databaseReference: DatabaseReference

    init {
        getCurrentDate()
        val injector: ViewModelInjector =
            DaggerViewModelInjector.builder().appModule(AppModule()).build()
        injector.inject(this)
    }

    fun saveDataInDb() {
        var refID: String? = null
        if (title.isNullOrEmpty() || date.isNullOrEmpty() || notes.isNullOrEmpty()) {
            authListener?.onFailure("Please enter all details")
            return
        }

        if (passedId != null)
            refID = passedId
        else refID = databaseReference.push().key

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                val note = Notes(title, date, notes, refID)
                databaseReference.child(refID!!).setValue(note)
                authListener?.onSuccess()

            }

            override fun onCancelled(@NonNull error: DatabaseError) {
                authListener?.onFailure(error.toString())
            }
        })
    }

    private fun getCurrentDate() {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        date = formattedDate
    }

    fun dateSelection(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            view.context,
            { _, year1, month1, day ->
                showDeliveryDate(year1, month1, day)
            }, year, month, dayOfMonth
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun showDeliveryDate(year1: Int, month1: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal[year1, month1, day, 0, 0] = 0
        val chosenStartDate = cal.time
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        date = dateFormat.format(chosenStartDate)
    }

    fun getNotesById(id1: String) {
        passedId = id1
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                if (passedId == null) return
                val name: Notes = snapshot.child(passedId!!).getValue(Notes::class.java)!!
                title = name.title
                notes = name.notes
                date = name.date
            }

            override fun onCancelled(@NonNull error: DatabaseError) {
                authListener?.onFailure(error.toString())
            }
        })
    }


}