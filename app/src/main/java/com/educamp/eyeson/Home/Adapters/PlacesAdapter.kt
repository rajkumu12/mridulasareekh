package com.educamp.eyeson.Home.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.educamp.eyeson.Home.Models.PlacesModel
import com.educamp.eyeson.R

class PlacesAdapter (val context: Context) :
    RecyclerView.Adapter<PlacesAdapter.MyViewHolder>() {

    private var placeslist: List<PlacesModel>? = null
    var row_index = 0
    var animBlink: Animation? = null

    fun setServices(placeslist: List<PlacesModel>?) {
        this.placeslist = placeslist
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlacesAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.places_itemview, parent, false)
        return MyViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: PlacesAdapter.MyViewHolder, position: Int) {
        holder.bind(placeslist?.get(position)!!, context)


    }

    override fun getItemCount(): Int {
        if (placeslist == null) return 0
        else return placeslist?.size!!
    }

    fun filterList(filterlist: List<PlacesModel>) {
        // below line is to add our filtered
        // list in our course array list.
        placeslist = filterlist.toMutableList()
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        @SuppressLint("SetTextI18n")
        fun bind(data: PlacesModel, activity: Context) {
            data.apply {

            }
        }
    }
}