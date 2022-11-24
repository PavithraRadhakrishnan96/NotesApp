package com.example.notesapp.addnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityAddNoteBinding
import com.example.notesapp.home.HomeViewModel
import com.example.notesapp.startHomeActivity

class AddNoteActivity : AppCompatActivity(), AuthListener {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddNoteBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_note)
        setViewModel(binding)
        getIntentData()
    }

    private fun setViewModel(binding: ActivityAddNoteBinding) {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.homeviewmodel = viewModel
        viewModel.authListener = this
    }

    private fun getIntentData() {
        val intent = intent.getStringExtra("id")
        if (intent != null) {
            viewModel.getNotesById(intent)
        }
    }


    override fun onStarted() {
//Progressbar not necessary
    }

    override fun onSuccess() {
        startHomeActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}