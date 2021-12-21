package com.rumeysaozer.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.rumeysaozer.chatapp.model.Mesajlar
import com.rumeysaozer.chatapp.R

class MesajAdapter(val context: Context, val messageList: ArrayList<Mesajlar>): RecyclerView.Adapter <RecyclerView.ViewHolder>(){
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.kullanici_konusma_recycler_row, parent, false)
            return AliciViewHolder(view)
        }
        else{
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.kullanici_konusma_recycler_row2, parent, false)
            return GondericiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == GondericiViewHolder::class.java){

            val viewHolder = holder as GondericiViewHolder
            holder.mesajGonder.text = currentMessage.mesaj
        }
        else{
            val viewHGolder = holder as AliciViewHolder
            holder.mesajAl.text = currentMessage.mesaj
        }
    }

    override fun getItemViewType(position: Int): Int {
      val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.gondericiId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }

    }
    override fun getItemCount(): Int {
        if(messageList.size >0){
            return messageList.size
        }
        else{
            return 0
        }

    }

    class GondericiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mesajGonder = itemView.findViewById<TextView>(R.id.kkrvTvgonder)
    }
    class AliciViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mesajAl = itemView.findViewById<TextView>(R.id.kkrvTval)
    }


}