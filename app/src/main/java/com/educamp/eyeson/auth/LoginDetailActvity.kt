package com.educamp.eyeson.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.educamp.bhinder.model.UserInfo
import com.educamp.eyeson.Home.HomeActivity
import com.educamp.eyeson.R
import com.educamp.eyeson.databinding.ActivityLoginDetailActvityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

class LoginDetailActvity : AppCompatActivity() {
    lateinit var binding: ActivityLoginDetailActvityBinding
    lateinit var progressDialog: ProgressDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    val RC_SIGN_IN = 123
    private var filePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginDetailActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = FirebaseStorage.getInstance();
        storageReference = storage!!.getReference();
        progressDialog = ProgressDialog(this)
        auth = Firebase.auth
        database = Firebase.database.reference
        binding.tvEmail.setText(intent.getStringExtra("email"))
        binding.tvUsername.setText(intent.getStringExtra("name"))



        binding.tvImage.setOnClickListener {
            getImage(binding.ivProfile)

        }

        binding.tvSubmit.setOnClickListener {

            if (binding.tvUsername.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (binding.tvEmail.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            } else if (binding.tvMobile.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
            } else if (binding.disability.getSelectedItem().toString()
                    .equals("Select disability")
            ) {
                Toast.makeText(this, "Select disability", Toast.LENGTH_SHORT).show()
            } else if (filePath == null) {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }

    private fun getImage(imgProfile: ImageView?) {

        showPictureDialog()

    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, 1)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    filePath = getImageUri(this, bitmap)
                    Toast.makeText(this@LoginDetailActvity, "Image Saved!", Toast.LENGTH_SHORT)
                        .show()
                    binding.ivProfile!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@LoginDetailActvity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == 2) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            binding.ivProfile!!.setImageBitmap(thumbnail)
            filePath = getImageUri(this, thumbnail)
            //Toast.makeText(this@LoginActivty, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    fun uploadImage() {
        progressDialog.setMessage("Creating profile please wait")
        progressDialog.show()
        val ref: StorageReference = storageReference!!.child(
            "images/"
                    + auth.currentUser?.uid
        )


        filePath?.let {
            ref.putFile(it)
                .addOnSuccessListener {

                    ref.getDownloadUrl()
                        .addOnSuccessListener(OnSuccessListener<Uri>
                        { downloadPhotoUrl -> //Now play with downloadPhotoUrl
                            val userRequest = UserInfo(
                                downloadPhotoUrl.toString(),
                                binding.tvUsername.text.toString(),
                                binding.tvEmail.text.toString(),
                                binding.tvMobile.text.toString(),
                                binding.disability.selectedItem.toString(),
                                "",
                                ""

                            )
                            database.child("User").child(auth.currentUser!!.uid)
                                .setValue(userRequest).addOnSuccessListener {
                                    progressDialog.dismiss()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                    Toast.makeText(
                                        this,
                                        "Profile created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }.addOnFailureListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        "Something went wrong try again",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }


                        })

                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    Toast
                        .makeText(
                            this@LoginDetailActvity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }


    }
}