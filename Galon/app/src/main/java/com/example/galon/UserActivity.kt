package com.example.galon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.example.galon.databinding.ActivityUserBinding
import com.example.galon.fragment.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imgUri : Uri

    lateinit var binding : ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        binding.cviUser.setOnClickListener {


        }
    }
}

