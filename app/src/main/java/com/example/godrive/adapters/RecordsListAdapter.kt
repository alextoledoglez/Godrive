package com.example.godrive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.godrive.R
import com.example.godrive.data.AppDatabase
import com.example.godrive.data.models.Person
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RecordsListAdapter(
    private val appDatabase: AppDatabase?,
    private val data: ArrayList<Person>
) :
    RecyclerView.Adapter<RecordsListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_record_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = data[position]

        val holderItemView = holder.itemView
        holderItemView.setOnClickListener { openRecordDetails() }

        val titleTextView = holderItemView.findViewById(R.id.text_view_title) as TextView
        titleTextView.text = person.name
        titleTextView.setOnClickListener { openRecordDetails() }

        val deleteImageButton = holderItemView.findViewById(R.id.image_button_delete) as ImageButton
        deleteImageButton.setOnClickListener {
            doAsync {
                appDatabase?.personDao()?.delete(person)
                uiThread {
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount() = data.size

    private fun openRecordDetails() {

    }
}