package com.rumeysaozer.chatapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rumeysaozer.chatapp.adapter.RecyclerAdapter
import com.rumeysaozer.chatapp.model.Sohbet
import com.rumeysaozer.chatapp.databinding.FragmentKonusmaBinding

class KonusmaFragment : Fragment() {
    private var _binding: FragmentKonusmaBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : RecyclerAdapter
    private var sohbetler = arrayListOf<Sohbet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKonusmaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RecyclerAdapter()
        binding.rvKonusma.adapter = adapter
        binding.rvKonusma.layoutManager = LinearLayoutManager(requireContext())


        binding.konusmaGonderButon.setOnClickListener {

            val mesaj = binding.konusmaText.text.toString()
            val kullanici = auth.currentUser!!.displayName
            val tarih = FieldValue.serverTimestamp()

            val dataMap = HashMap<String, Any>()
            dataMap.put("mesaj", mesaj)
            dataMap.put("kullanici", kullanici!!)
            dataMap.put("tarih", tarih)



            firestore.collection("Sohbetler").add(dataMap).addOnSuccessListener {
                binding.konusmaText.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                binding.konusmaText.setText("")
            }
        }
        firestore.collection("Sohbetler").orderBy("tarih", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error != null){

            }
            else{
                if(value != null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(), "Mesaj yok", Toast.LENGTH_LONG).show()
                    }
                    else{
                        val documents = value.documents
                        sohbetler.clear()
                        for(document in documents){
                           val mesaj = document.get("mesaj") as String
                            val kullanici = document.get("kullanici") as String?
                            val sohbet = Sohbet(kullanici!!,mesaj)
                            sohbetler.add(sohbet)
                            adapter.sohbetler = sohbetler


                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
