package com.rumeysaozer.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.rumeysaozer.chatapp.R
import com.rumeysaozer.chatapp.model.Sohbet

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.SohbetHolder>() {
    private val view_type_message_send = 1
    private val view_type_message_get = 2


    class SohbetHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    private val diffUtil = object : DiffUtil.ItemCallback<Sohbet>(){
        override fun areItemsTheSame(oldItem: Sohbet, newItem: Sohbet): Boolean {
        return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Sohbet, newItem: Sohbet): Boolean {
            return oldItem == newItem
        }

    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)
    var sohbetler : List<Sohbet>
    get ()=recyclerListDiffer.currentList
        set(value)= recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {

        val sohbet = sohbetler.get(position)
        if(sohbet.kullanici == FirebaseAuth.getInstance().currentUser?.displayName.toString()){
            return view_type_message_send
        }else{
            return view_type_message_get
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SohbetHolder {
        if(viewType == view_type_message_get){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent, false)
            return SohbetHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_2,parent, false)
            return SohbetHolder(view)
        }

    }

    override fun onBindViewHolder(holder: SohbetHolder, position: Int) {

        val textView = holder.itemView.findViewById<TextView>(R.id.rvTv)
        textView.text = "${sohbetler.get(position).kullanici}: ${sohbetler.get(position).mesaj}"
    }

    override fun getItemCount(): Int {
        return sohbetler.size
    }
}