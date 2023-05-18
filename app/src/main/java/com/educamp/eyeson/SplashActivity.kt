package com.educamp.eyeson

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.educamp.eyeson.Home.HomeActivity
import com.educamp.eyeson.auth.LoginActivty
import com.educamp.eyeson.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var ref: MediaPlayer
    lateinit var singletonAudio: SingletonAudio
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        singletonAudio = SingletonAudio.getInstance()
        singletonAudio.init(this)
        ref = SingletonAudio.getSingletonMedia(R.raw.welcomesound);
        ref.start()
        Glide.with(this).asGif().load(R.raw.vid).into(binding.ivMain)

        Timer().schedule(5000) {

                 if (firebaseAuth.currentUser !=null){
                 startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                 finish()
                 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
             }else{
                 startActivity(Intent(this@SplashActivity,LoginActivty::class.java))
                 finish()
                 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
             }

           /* startActivity(Intent(this@SplashActivity, LoginActivty::class.java))
            finish()*/
        }

    }

    override fun onStop() {
        super.onStop()
    }
}