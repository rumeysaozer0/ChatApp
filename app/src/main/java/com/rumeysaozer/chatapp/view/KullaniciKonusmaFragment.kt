package com.rumeysaozer.chatapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rumeysaozer.chatapp.R
import com.rumeysaozer.chatapp.model.SonMesajlar
import com.rumeysaozer.chatapp.databinding.FragmentKullaniciKonusmaBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_kullanici_konusma.*
import kotlinx.android.synthetic.main.kullanici_konusma_recycler_row.view.*
import kotlinx.android.synthetic.main.kullanici_konusma_recycler_row2.view.*


class KullaniciKonusmaFragment : Fragment() {
    companion object{
        val TAG ="KullaniciKonusma"


    }
    val adapter = GroupAdapter<ViewHolder>()
    private var kullaniciAdi = "ali"
    private var kullaniciId = "1"
    private var imageUrl = ""


    private lateinit var database : FirebaseFirestore
    private var _binding: FragmentKullaniciKonusmaBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.firestore
        database = FirebaseFirestore.getInstance()

    }
 override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciKonusmaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {
            kullaniciAdi = KullaniciKonusmaFragmentArgs.fromBundle(it).kullaniciAdi
            kullaniciId = KullaniciKonusmaFragmentArgs.fromBundle(it).kullaniciId
            imageUrl = KullaniciKonusmaFragmentArgs.fromBundle(it).imageUrl

        }

        (activity as AppCompatActivity).supportActionBar?.title = "${kullaniciAdi}"


        binding.rvKullaniciKonusma.adapter = adapter



        binding.kullaniciKonusmaGonderButon.setOnClickListener {
            Log.d(TAG, "attemp to send message")
            performSendMessage()
        }
        val fromId = FirebaseAuth.getInstance().uid
        val toId = kullaniciId


        database.collection("Mesajlar/$fromId/$toId").orderBy("tarih", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error != null){

            }
            else{
                if(value != null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(), "Mesaj yok", Toast.LENGTH_LONG).show()
                    }
                    else{
                        val documents = value.documents
                            adapter.clear()
                        for(document in documents){
                            val mesaj = document.get("text") as String
                            val fromId = document.get("fromId") as String
                            if(fromId != FirebaseAuth.getInstance().uid ){
                                adapter.add(ChatFromItem(mesaj))
                            }
                            else{
                                adapter.add(ChatToItem(mesaj))
                            }



                        }
                        adapter.notifyDataSetChanged()


                    }
                }

            }
        }


    }


    private fun performSendMessage(){
         val text = kullaniciKonusmaText.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = kullaniciId
        val tarih = FieldValue.serverTimestamp()
        if(fromId == null) return
        val kayitHashMap = hashMapOf<String, Any>()
        kayitHashMap.put("text" ,text)
        kayitHashMap.put("fromId" ,fromId)
        kayitHashMap.put("toId" ,toId)
        kayitHashMap.put("tarih" ,tarih)

        database.collection("Mesajlar/$fromId/$toId").add(kayitHashMap).addOnSuccessListener {
            binding.kullaniciKonusmaText.setText("")
            binding.rvKullaniciKonusma.scrollToPosition(adapter.itemCount -1)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            binding.kullaniciKonusmaText.setText("")
        }


        database.collection("Mesajlar/$toId/$fromId").add(kayitHashMap).addOnSuccessListener {
            binding.kullaniciKonusmaText.setText("")
            binding.rvKullaniciKonusma.scrollToPosition(adapter.itemCount -1)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            binding.kullaniciKonusmaText.setText("")
        }


        val sonMesajlar = SonMesajlar(kullaniciId,text,fromId,toId, System.currentTimeMillis()/1000, kullaniciAdi, imageUrl)
        val sonMesajlarRef = FirebaseDatabase.getInstance().getReference("/SonMesajlar/$fromId/$toId")
        sonMesajlarRef.setValue(sonMesajlar)
        val sonMesajlarToRef = FirebaseDatabase.getInstance().getReference("/SonMesajlar/$toId/$fromId")
        sonMesajlarToRef.setValue(sonMesajlar)

    }

    class ChatFromItem(val text:String): Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.kkrvTval.text = text
        }
        override fun getLayout(): Int {
            return R.layout.kullanici_konusma_recycler_row
        }
    }


    class ChatToItem(val text: String): Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.kkrvTvgonder.text = text
        }
        override fun getLayout(): Int {
            return R.layout.kullanici_konusma_recycler_row2
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.getItemId() == android.R.id.home){
            val action = KullaniciKonusmaFragmentDirections.actionKullaniciKonusmaFragmentToSonMesajlarFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    }
