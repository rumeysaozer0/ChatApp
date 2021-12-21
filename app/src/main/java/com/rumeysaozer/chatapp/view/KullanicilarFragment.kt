package com.rumeysaozer.chatapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rumeysaozer.chatapp.adapter.KullanicilarRecyclerAdapter
import com.rumeysaozer.chatapp.model.Kullanicilar
import com.rumeysaozer.chatapp.databinding.FragmentKullanicilarBinding



class KullanicilarFragment : Fragment() {
    private var _binding: FragmentKullanicilarBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : KullanicilarRecyclerAdapter
    private var Kullanicilar = arrayListOf<Kullanicilar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.firestore
        auth = Firebase.auth


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullanicilarBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = KullanicilarRecyclerAdapter(Kullanicilar)
        binding.kullanicilarRV.adapter = adapter
        binding.kullanicilarRV.layoutManager = LinearLayoutManager(requireContext())
        getData()
        (activity as AppCompatActivity).supportActionBar?.title = "Kullanıcılar"




    }
    fun getData(){
        database.collection("Kullanicilar").addSnapshotListener { value, error ->
            if(error != null){

            }
            else{
                if(value != null){
                    if(value.isEmpty == false){
                        val documents = value.documents
                        Kullanicilar.clear()
                        for(document in documents){
                            val kullaniciUuid = document.get("kullaniciUuid") as String
                            val kullaniciAdi = document.get("kullaniciAdi") as String
                            val imageUrl = document.get("imageUrl") as String
                            val indirilen = Kullanicilar(kullaniciAdi,imageUrl, kullaniciUuid )
                            Kullanicilar.add(indirilen)




                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }


    }




override fun onDestroy() {
    super.onDestroy()
    _binding = null
}

}