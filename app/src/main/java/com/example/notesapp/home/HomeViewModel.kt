package com.example.notesapp.home

import android.app.DatePickerDialog
import android.content.Intent
import androidx.lifecycle.LiveData

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.addnote.AddNoteActivity
import com.example.notesapp.addnote.Notes
import com.google.firebase.database.*
import io.reactivex.annotations.NonNull
import java.text.DateFormat
import java.util.*
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat


class HomeViewModel : ViewModel() {
    var authListener: AuthListener? = null
    var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var title: String? = null
    var date: String? = null
    var notes: String? = null
    var passedId: String? = null
    private var userLiveData = MutableLiveData<List<Notes>>()

    init {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        date=formattedDate

    }

    fun gotoAddNote(view: View) {
        Intent(view.context, AddNoteActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun deleteAllData() {
        rootRef.removeValue()
    }


    fun saveDataInDb() {

        if (title.isNullOrEmpty() || date.isNullOrEmpty() || notes.isNullOrEmpty()) {
            authListener?.onFailure("Please enter all details")
            return
        }

        if(passedId!=null){
            rootRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                    if(passedId==null)return
                    val note = Notes(title, date, notes,passedId)
                    rootRef.child(passedId!!).setValue(note)
                    authListener?.onSuccess()

                }
                override fun onCancelled(@NonNull error: DatabaseError) {
                    authListener?.onFailure(error.toString())
                }
            })

        }else{
            val uId= rootRef.push().key
            rootRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                    val note = Notes(title, date, notes,uId)
                    rootRef.child(uId!!).setValue(note)
                    authListener?.onSuccess()
                }
                override fun onCancelled(@NonNull error: DatabaseError) {
                    authListener?.onFailure(error.toString())
                }
            })

        }
    }

    fun dateSelection(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(view.context,
            { _, year1, month1, day ->
                showDeliveryDate(year1, month1, day)
            }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun showDeliveryDate(year1: Int, month1: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal[year1, month1, day, 0, 0] = 0
        val chosenStartDate = cal.time
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        date=dateFormat.format(chosenStartDate)
    }


    fun getResponseFromRealtimeDatabaseUsingLiveData(): LiveData<List<Notes>> {
        authListener?.onStarted()

        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userLiveData.value = dataSnapshot.children.map { snapShot ->
                    snapShot.getValue(Notes::class.java)!!
                }
                authListener?.onSuccess()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                authListener?.onFailure(databaseError.message)
            }

        })

        return userLiveData
    }

    fun getNotesById(id1: String) {
        passedId = id1
        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                if(passedId==null)return
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

    override fun onCleared() {
        super.onCleared()
    }

}