package com.example.galon.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.galon.LoginActivity
import com.example.galon.MainActivity
import com.example.galon.databinding.FragmentUserBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UserFragment : Fragment() {

    private var _binding : FragmentUserBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var imgUri : Uri

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cviUser.setOnClickListener{
            goToCamera()
        }

        if (user != null){
            binding.etdName.setText(user.displayName)
            binding.etEmail.setText(user.email)

            if (user.photoUrl != null){
                Picasso.get().load(user.photoUrl).into(binding.cviUser)
            }else{
                Picasso.get().load("https://picsum.photos/id/316/200").into(binding.cviUser)
            }

            if (user.isEmailVerified){
                binding.icVerified.visibility = View.VISIBLE
            }else{
                binding.icUnverified.visibility = View.VISIBLE
            }

        }

        binding.btnSave.setOnClickListener{
            val image = when{
                :: imgUri.isInitialized -> imgUri
                user?.photoUrl == null -> Uri.parse("https://picsum.photos/id/316/200")
                else -> user.photoUrl
            }

            val name = binding.etdName.text.toString().trim()

            if (name.isEmpty()){
                binding.etdName.error = "Nama Harus diisi"
                binding.etdName.requestFocus()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)

                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        binding.btnLogout.setOnClickListener{
            btnLogout()
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.icUnverified.setOnClickListener{
            user?.sendEmailVerification()?.addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(activity, "Email Verifikasi Sudah dikitim", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnchangePassword.setOnClickListener{
            changePassword()
        }

    }

    private fun changePassword() {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cvCurrentPassword.visibility = View.VISIBLE
        binding.cvUser.visibility = View.GONE
        binding.bottom.visibility = View.GONE

        binding.btnCancel.setOnClickListener {
            binding.cvCurrentPassword.visibility = View.GONE
            binding.cvUser.visibility = View.VISIBLE
            binding.bottom.visibility = View.VISIBLE
        }

        binding.btnConfirm.setOnClickListener btnConfim@{
            val pass = binding.edtCurrentPassword.text.toString()
            if (pass.isEmpty()){
                binding.edtCurrentPassword.error = "Password Tidak Boleh Kosong"
                binding.edtCurrentPassword.requestFocus()
                return@btnConfim
            }
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when{
                        task.isSuccessful -> {
                            binding.cvCurrentPassword.visibility = View.GONE
                            binding.cvUpdatePassword.visibility = View.VISIBLE
                        }

                        task.exception is FirebaseAuthInvalidCredentialsException ->{
                            binding.edtCurrentPassword.error = "Password Salah"
                            binding.edtCurrentPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }

            binding.btnNewCancel.setOnClickListener{
                binding.cvCurrentPassword.visibility = View.GONE
                binding.cvUpdatePassword.visibility = View.GONE
                binding.cvUser.visibility = View.VISIBLE
            }

            binding.btnChange.setOnClickListener newChangePass@{
                val newPass = binding.newPass.text.toString()
                val passConf = binding.newConfPass.text.toString()

                if (newPass.isEmpty()){
                    binding.edtCurrentPassword.error = "Password Baru Tidak Boleh Kosong"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePass
                }

                if (passConf.isEmpty()){
                    binding.edtCurrentPassword.error = "Ulangi Password Baru"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePass
                }

                if (newPass.length < 6){
                    binding.edtCurrentPassword.error = "Password Minimal 6 Character"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePass
                }

                if (newPass != passConf){
                    binding.edtCurrentPassword.error = "Password Tidak Sama"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePass
                }

                user?.let {
                    user.updatePassword(newPass).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Password Berhasil Dirubah", Toast.LENGTH_SHORT).show()
                            sucssesLogout()
                        }else{
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun sucssesLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()

        Toast.makeText(activity, "Silahkan Login Kembali", Toast.LENGTH_SHORT).show()
    }

    private fun goToCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK){
            val imgbitmap = data?.extras?.get("data") as Bitmap
            uploadImageToFirebase(imgbitmap)
        }
    }

    //upload gambar
    private fun uploadImageToFirebase(imgbitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img_user/${FirebaseAuth.getInstance().currentUser?.email}")
        imgbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val img = baos.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    ref.downloadUrl.addOnCompleteListener{
                        Task->
                        Task.result.let { Uri->
                            imgUri = Uri
                            binding.cviUser.setImageBitmap(imgbitmap)
                        }
                    }
                }
            }
    }


    private fun btnLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object{
        const val REQ_CAM = 100
    }

}