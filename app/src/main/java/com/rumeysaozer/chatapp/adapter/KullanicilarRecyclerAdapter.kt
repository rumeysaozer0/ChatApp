package com.rumeysaozer.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.rumeysaozer.chatapp.model.Kullanicilar
import com.rumeysaozer.chatapp.R
import com.rumeysaozer.chatapp.view.KullanicilarFragmentDirections
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class KullanicilarRecyclerAdapter(val kullaniciList : ArrayList<Kullanicilar>) : RecyclerView.Adapter<KullanicilarRecyclerAdapter.KullaniciHolder>() {
    class KullaniciHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KullaniciHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_recycler_row, parent, false)
        return KullaniciHolder(view)
    }


    override fun getItemCount(): Int {
        return kullaniciList.size

    }

    override fun onBindViewHolder(holder: KullaniciHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.krr_name)
        textView.text = kullaniciList[position].kullaniciAdi
        val imageView = holder.itemView.findViewById<CircleImageView>(R.id.krr_image)
        Picasso.get().load(kullaniciList[position].imageUrl).into(imageView)
        holder.itemView.setOnClickListener {
        val action = KullanicilarFragmentDirections.actionKullanicilarFragmentToKullaniciKonusmaFragment(kullaniciList.get(position).kullaniciAdi
            ,kullaniciList.get(position).kullaniciUuid, kullaniciList.get(position).imageUrl
          )
            Navigation.findNavController(it).navigate(action)
        }
    }
}
