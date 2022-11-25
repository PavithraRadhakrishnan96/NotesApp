package com.example.notesapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.auth.AuthListener
import com.example.notesapp.R
import com.example.notesapp.addnote.Notes
import com.example.notesapp.databinding.ActivityHomeBinding
import java.util.*
import kotlin.collections.ArrayList
import com.example.notesapp.startLoginActivity


class HomeActivity : AppCompatActivity(), AuthListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeScreenAdapter: HomeScreenAdapter
    private var notesLiveData = MutableLiveData<List<Notes>>()


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

        viewModel.getResponse().observe(this, { response ->
            if (response.isEmpty()) noRecord()
            notesLiveData.value=response
            homeScreenAdapter.setNoteList(response)

        })
    }

    private fun noRecord() {
        binding.tvErrorMessage.visibility=View.VISIBLE
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)
        val actionlogout: MenuItem = menu.findItem(R.id.actionlogout)
        val searchView: SearchView = searchItem.actionView as SearchView


        actionlogout.setOnMenuItemClickListener {
            viewModel.logout()
            startLoginActivity()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }
        })
        return true
    }
    private fun filter(text: String) {
        val filteredList: ArrayList<Notes> = ArrayList<Notes>()
        for (item in notesLiveData.value!!) {
            if (item.title?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            homeScreenAdapter.setNoteList(filteredList)
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            homeScreenAdapter.setNoteList(filteredList)
        }
    }


}