package com.rumeysaozer.chatapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.rumeysaozer.chatapp.databinding.FragmentKayitOlBinding
import java.util.*

class kayitOlFragment : Fragment() {
    private var _binding: FragmentKayitOlBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    var selectedImage : Uri? = null
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.firestore
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKayitOlBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.kayitOlButon.setOnClickListener {

            val email = binding.kEmailText.text.toString()
            val parola = binding.kPasswordText.text.toString()
            if (parola != "" && email != "") {
                auth.createUserWithEmailAndPassword(email, parola).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //kullanici adi güncelle
                        val kullaniciAdi = binding.kKullaniciAdiText.text.toString()
                        val guncelKullanici = auth.currentUser
                        val profilGuncellemesi = userProfileChangeRequest {
                            displayName = kullaniciAdi

                        }
                        guncelKullanici?.updateProfile(profilGuncellemesi)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Kayıt Başarılı. Hoşgeldiniz ${kullaniciAdi}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }


                        val uuid = UUID.randomUUID()
                        val imageName = "${uuid}.jpg"
                        val reference = storage.reference
                        val imageReference = reference.child("images").child(imageName)
                        if (selectedImage != null) {
                            imageReference.putFile(selectedImage!!).addOnSuccessListener {
                                val uploadedImageReference =
                                    FirebaseStorage.getInstance().reference.child("images")
                                        .child(imageName)
                                uploadedImageReference.downloadUrl.addOnSuccessListener {
                                    val KUuid = FirebaseAuth.getInstance().uid
                                    val downloadUrl = it.toString()
                                    val email = binding.kEmailText.text.toString()
                                    val kullaniciAdi = binding.kKullaniciAdiText.text.toString()
                                    val password = binding.kPasswordText.text.toString()
                                    val tarih = Timestamp.now()
                                    val kullaniciUuid = KUuid

                                    val kayitHashMap = hashMapOf<String, Any>()
                                    kayitHashMap.put("imageUrl", downloadUrl)
                                    kayitHashMap.put("email", email)
                                    kayitHashMap.put("kullaniciAdi", kullaniciAdi)
                                    kayitHashMap.put("password", password)
                                    kayitHashMap.put("tarih", tarih)
                                    if (kullaniciUuid != null) {
                                        kayitHashMap.put("kullaniciUuid", kullaniciUuid)
                                    }

                                    database.collection("Kullanicilar").add(kayitHashMap)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {


                                            }
                                        }.addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            it.localizedMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                                }.addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        it.localizedMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }


                        val action =
                            kayitOlFragmentDirections.actionKayitOlFragmentToSonMesajlarFragment()
                        findNavController().navigate(action)
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                }


            }

            binding.imageAdd.setOnClickListener {
                activity?.let {


                    if (ContextCompat.checkSelfPermission(
                            it.applicationContext,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1
                        )
                    } else {

                        var galeriIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(galeriIntent, 2)
                    }

                }
            }

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                var galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            selectedImage = data.data
            try{
                context?.let{
                    if(selectedImage != null && context != null) {
                        if(Build.VERSION.SDK_INT >=28) {


                            val source = ImageDecoder.createSource(it.contentResolver, selectedImage!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageAdd.setImageBitmap(selectedBitmap)
                        }
                        else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, selectedImage)
                            binding.imageAdd.setImageBitmap(selectedBitmap)
                        }
                    }
                }

            }catch(e: Exception){
                e.printStackTrace()

            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

}



