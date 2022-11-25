package com.example.notesapp.home

import android.content.Intent
import androidx.lifecycle.LiveData
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.addnote.AddNoteActivity
import com.example.notesapp.addnote.Notes
import com.google.firebase.database.*
import androidx.lifecycle.MutableLiveData
import com.example.notesapp.di.AppModule
import com.example.notesapp.di.DaggerViewModelInjector
import com.example.notesapp.di.ViewModelInjector
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth


class HomeViewModel : ViewModel() {
    var authListener: AuthListener? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var databaseReference: DatabaseReference

    private var notesLiveData = MutableLiveData<List<Notes>>()

    init {
        val injector: ViewModelInjector =
            DaggerViewModelInjector.builder().appModule(AppModule()).build()
        injector.inject(this)
        getResponse()

    }

    fun gotoAddNote(view: View) {
        Intent(view.context, AddNoteActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun deleteAllData() {
        databaseReference.removeValue()
    }


     fun getResponse() : LiveData<List<Notes>> {
        authListener?.onStarted()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notesLiveData.value = dataSnapshot.children.map { snapShot ->
                    snapShot.getValue(Notes::class.java)!!
                }
                authListener?.onSuccess()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                authListener?.onFailure(databaseError.message)
            }

        })
        return notesLiveData
    }


     fun logout() {
         firebaseAuth.signOut()
     }




}