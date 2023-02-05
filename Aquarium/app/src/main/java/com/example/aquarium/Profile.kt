package com.example.aquarium

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logoutButton = view.findViewById<Button>(R.id.logout_btn)
        val editButton = view.findViewById<Button>(R.id.edit_profil_btn)
        val cardProfile = view.findViewById<CardView>(R.id.card_profile)
        val editCard = view.findViewById<CardView>(R.id.card_edit_profile)
        val saveButton = view.findViewById<Button>(R.id.save_btn)
        val cancleButton = view.findViewById<Button>(R.id.cancel_btn_profile)
        val et_newPass = view.findViewById<EditText>(R.id.et_new_pass)
        val et_oldPass = view.findViewById<EditText>(R.id.et_old_pass)
        val et_username = view.findViewById<EditText>(R.id.et_username)

        val username = view.findViewById<TextView>(R.id.username)
        val userId = Firebase.auth.currentUser?.uid.toString()
        FirebaseDatabase.getInstance().getReference(userId).child("username")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                     username.text = snapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        editButton.setOnClickListener {
            cardProfile.visibility = View.GONE
            editCard.visibility = View.VISIBLE
        }

        cancleButton.setOnClickListener {
            et_newPass.text.clear()
            et_oldPass.text.clear()
            et_username.text.clear()

            editCard.visibility = View.GONE
            cardProfile.visibility = View.VISIBLE

        }

        saveButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val auth = Firebase.auth
        var newPass = view?.findViewById<EditText>(R.id.et_new_pass)?.text.toString()
        var oldPass = view?.findViewById<EditText>(R.id.et_old_pass)?.text.toString()
        var username = view?.findViewById<EditText>(R.id.et_username)?.text.toString()
        val userId = auth.currentUser?.uid.toString()
        val et_newPass = view?.findViewById<EditText>(R.id.et_new_pass)
        val et_oldPass = view?.findViewById<EditText>(R.id.et_old_pass)
        val et_username = view?.findViewById<EditText>(R.id.et_username)
        val cardProfile = view?.findViewById<CardView>(R.id.card_profile)
        val editCard = view?.findViewById<CardView>(R.id.card_edit_profile)
        val Email = auth.currentUser?.email.toString()
        val credential : AuthCredential = EmailAuthProvider
            .getCredential(Email, oldPass)

        if (oldPass.isEmpty()||newPass.isEmpty()){
            Toast.makeText(activity, "Password Lama dan Password Baru Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
        }

        else if (oldPass.equals(newPass)){
            Toast.makeText(activity, "Password Lama dan Password Baru Tidak Boleh Sama", Toast.LENGTH_SHORT).show()
        }

        else if (oldPass.length < 6 || newPass.length < 6){
            Toast.makeText(activity, "Password Tidak Kurang Dari 6 Karakter", Toast.LENGTH_SHORT).show()
        }

        else {
            if (username.isEmpty()){
                auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful){
                            auth.currentUser?.updatePassword(newPass)?.addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(activity, "Profil Berhasil Diganti", Toast.LENGTH_SHORT).show()
                                    if (et_newPass != null) {
                                        et_newPass.text.clear()
                                    }
                                    if (et_oldPass != null) {
                                        et_oldPass.text.clear()
                                    }
                                    if (et_username != null) {
                                        et_username.text.clear()
                                    }

                                    editCard?.visibility = View.GONE
                                    cardProfile?.visibility = View.VISIBLE
                                }
                            }
                        }

                    else {
                            Toast.makeText(activity, "Password lama anda salah", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            else{
                FirebaseDatabase.getInstance().getReference(userId)
                    .child("username").setValue(username)
                auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                    if (it.isSuccessful){
                        auth.currentUser?.updatePassword(newPass)?.addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(activity, "Profil Berhasil Diganti", Toast.LENGTH_SHORT).show()
                                if (et_newPass != null) {
                                    et_newPass.text.clear()
                                }
                                if (et_oldPass != null) {
                                    et_oldPass.text.clear()
                                }
                                if (et_username != null) {
                                    et_username.text.clear()
                                }

                                editCard?.visibility = View.GONE
                                cardProfile?.visibility = View.VISIBLE
                            }
                        }
                    }

                    else{
                        Toast.makeText(activity, "Password lama anda salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



    }
}