package com.educamp.eyeson.auth

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.educamp.eyeson.Home.HomeActivity
import com.educamp.eyeson.R
import com.educamp.eyeson.SingletonAudio
import com.educamp.eyeson.databinding.ActivityLoginActivtyBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class LoginActivty : AppCompatActivity() {
    lateinit var ref: MediaPlayer
    lateinit var singletonAudio: SingletonAudio
    lateinit var progressDialog: ProgressDialog
    private lateinit var auth: FirebaseAuth
    lateinit var loginbtn: CardView
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 123
    private lateinit var database: DatabaseReference
    lateinit var img_profile: CircleImageView

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    private var filePath: Uri? = null


    lateinit var binding: ActivityLoginActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPermissions()
        storage = FirebaseStorage.getInstance();
        storageReference = storage!!.getReference();
        progressDialog = ProgressDialog(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        database = Firebase.database.reference

        binding.loginbtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            // startActivity(Intent(this@LoginActivty,LoginDetailActvity::class.java))
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("sdgjgfsgsfgg", "ddd" + account.email)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Ambitiontag", "Google sign in failed", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        singletonAudio = SingletonAudio.getInstance()
        singletonAudio.init(this)
        ref = SingletonAudio.getSingletonMedia(R.raw.logininstruction);
        ref.start()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(ContentValues.TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            3
        )
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser = auth.getCurrentUser()!!

                        database.child("User").child(user.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                val intent = Intent(this, LoginDetailActvity::class.java)
                                intent.putExtra("name", acct.displayName)
                                intent.putExtra("email", acct.email)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener {
                            Log.e("firebase", "Error getting data", it)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication Failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }

}