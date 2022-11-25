package com.example.notesapp.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.addnote.AddNoteActivity
import com.example.notesapp.addnote.Notes
import com.example.notesapp.databinding.LayoutHomeBinding

class HomeScreenAdapter(var context:HomeActivity) : RecyclerView.Adapter<HomeScreenAdapter.ViewHolder>() {

    private var noteList = ArrayList<Notes>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutHomeBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitle.text = noteList[position].title
        holder.binding.tvDate.text = noteList[position].date
        holder.binding.tvNotes.text = noteList[position].notes
        holder.binding.cvNotes.setOnClickListener {
            Intent(context, AddNoteActivity::class.java).also {
                it.putExtra("id", noteList[position].id)
                context.startActivity(it)

            }

        }
    }
    class ViewHolder(val binding: LayoutHomeBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return noteList.size
    }
    fun setNoteList(characters: List<Notes>) {
        this.noteList = characters as ArrayList<Notes>
        notifyDataSetChanged()
    }



}