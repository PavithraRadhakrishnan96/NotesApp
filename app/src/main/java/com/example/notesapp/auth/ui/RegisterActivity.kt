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
import com.example.notesapp.databinding.ActivityRegisterBinding
import com.example.notesapp.startHomeActivity

class RegisterActivity : AppCompatActivity(), AuthListener {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
    }

    private fun setViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.viewmodel = viewModel
        viewModel.authListener = this
    }

    override fun onStarted() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        binding.progressbar.visibility = View.GONE
        viewModel.currentUser().let {
            startHomeActivity()
        }
        Toast.makeText(this, "Registered Successfully...", Toast.LENGTH_SHORT).show()

    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.progressbar.visibility = View.GONE

    }
}