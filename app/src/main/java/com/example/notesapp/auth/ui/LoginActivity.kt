package com.example.notesapp.auth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.auth.viewmodel.AuthViewModel
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.startHomeActivity

class LoginActivity : AppCompatActivity(), AuthListener {
    private lateinit var viewModel: AuthViewModel
    private lateinit var  binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.viewmodel = viewModel
        viewModel.authListener = this
        viewModel.currentUser()?.let {
            startHomeActivity()
        }
    }

    override fun onStarted() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        binding.progressbar.visibility=View.GONE
        Toast.makeText(this, "Logged In Successfully...", Toast.LENGTH_SHORT).show()
        viewModel.currentUser()?.let {
            startHomeActivity()
        }
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility=View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}