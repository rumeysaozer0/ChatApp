package com.rumeysaozer.chatapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rumeysaozer.chatapp.databinding.FragmentGirisBinding


class GirisFragment : Fragment() {

    private var _binding: FragmentGirisBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if(currentUser != null){
            val action = GirisFragmentDirections.actionGirisFragmentToSonMesajlarFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGirisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonKayitOl.setOnClickListener {
            val action = GirisFragmentDirections.actionGirisFragmentToKayitOlFragment()
            findNavController().navigate(action)

        }

        binding.buttonGirisYap.setOnClickListener {
            val email = binding.mailText.text.toString()
            val parola = binding.passwordText.text.toString()
            if (parola != "" && email != "") {
                auth.signInWithEmailAndPassword(email, parola).addOnSuccessListener {
                    val action = GirisFragmentDirections.actionGirisFragmentToSonMesajlarFragment()
                    findNavController().navigate(action)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}