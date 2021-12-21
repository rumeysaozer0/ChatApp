package com.rumeysaozer.chatapp.view



import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rumeysaozer.chatapp.R
import com.rumeysaozer.chatapp.model.SonMesajlar
import com.rumeysaozer.chatapp.databinding.FragmentSonMesajlarBinding
import com.rumeysaozer.chatapp.model.Kullanicilar
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.son_mesajlar_row.view.*

class SonMesajlarFragment : Fragment() {
    private lateinit var database : FirebaseFirestore
    private var _binding: FragmentSonMesajlarBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {

        database = Firebase.firestore
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSonMesajlarBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    class SonMesajlarRow(val mesaj: SonMesajlar): Item<ViewHolder>(){


        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.sonMesajlarMesaj.text = mesaj.text
            viewHolder.itemView.sonMesajlarKullaniciAdi.text = mesaj.userName

            val image =  viewHolder.itemView.sonMesajlarRowİmageView
            Picasso.get().load(mesaj.imageUrl).into(image)
        }

        override fun getLayout(): Int {
          return R.layout.son_mesajlar_row
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForSonMesajlar()
        (activity as AppCompatActivity).supportActionBar?.title = "Púrpura"
        binding.recyclerViewSonMesajlar.adapter = adapter

        binding.recyclerViewSonMesajlar.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->



        val row = item as SonMesajlarRow
            val action = SonMesajlarFragmentDirections.actionSonMesajlarFragmentToKullaniciKonusmaFragment( row.mesaj.userName, row.mesaj.toId, row.mesaj.imageUrl )
            findNavController().navigate(action)

        }




    }

    val sonMesajlarMap = HashMap<String, SonMesajlar>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        sonMesajlarMap.values.forEach {
            adapter.add(SonMesajlarRow(it))
        }
    }


        private fun listenForSonMesajlar(){
            val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/SonMesajlar/$fromId")
        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

               val mesaj = snapshot.getValue(SonMesajlar::class.java) ?:return
                sonMesajlarMap[snapshot.key!!] = mesaj
                refreshRecyclerViewMessages()


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val mesaj = snapshot.getValue(SonMesajlar::class.java) ?:return
                sonMesajlarMap[snapshot.key!!] = mesaj
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val mesaj = snapshot.getValue(SonMesajlar::class.java) ?:return
                sonMesajlarMap[snapshot.key!!] = mesaj
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        }
    val adapter = GroupAdapter<ViewHolder>()




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.yeni_mesaj ->{
                val action = SonMesajlarFragmentDirections.actionSonMesajlarFragmentToKullanicilarFragment()
                findNavController().navigate(action)
            }
            R.id.cikis_yap ->{
            FirebaseAuth.getInstance().signOut()

                val action = SonMesajlarFragmentDirections.actionSonMesajlarFragmentToGirisFragment()
                findNavController().navigate(action)

            }
            R.id.genel_mesaj ->{
                val action = SonMesajlarFragmentDirections.actionSonMesajlarFragmentToKonusmaFragment()
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}
