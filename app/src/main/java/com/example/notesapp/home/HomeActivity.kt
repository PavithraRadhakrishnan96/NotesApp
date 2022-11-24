package com.example.notesapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), AuthListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeScreenAdapter: HomeScreenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        prepareRecyclerView(binding)
        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.homeviewmodel = viewModel
        viewModel.authListener = this

        viewModel.getResponseFromRealtimeDatabaseUsingLiveData().observe(this, { response ->
            if (response.isEmpty()) onFailure("Notes not added yet....")
            homeScreenAdapter.setNoteList(response)

        })
    }

    private fun prepareRecyclerView(binding: ActivityHomeBinding) {
        homeScreenAdapter = HomeScreenAdapter(this)
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = homeScreenAdapter
        }
    }

    override fun onStarted() {
        binding.progressbar.visibility=View.VISIBLE
    }

    override fun onSuccess() {
        binding.progressbar.visibility=View.GONE
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility=View.GONE
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()

    }


}