package com.alfanshter.aplikasiiska.home.ui.MenuUpload.foto

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.Session.Permissions
import com.alfanshter.aplikasiiska.home.bottonNavigation
import com.alfanshter.aplikasiiska.home.ui.MenuUpload.DetailFotoActivity
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_detail_foto.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*


class GalleryFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1
    private var mStorage: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var myUrl = ""
    private var mUserId: String? = null
    private var mDatabase: DatabaseReference? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    private lateinit var rv_cerita : RelativeLayout
    private lateinit var btn_kamera : Button
    private lateinit var btn_close : ImageButton
    private lateinit var btn_check : ImageButton
    private lateinit var  edit_cerita : EditText
    private var filePath: String? = null
    var logic = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        sessionManager = SessionManager(context?.applicationContext)
        progressDialog = ProgressDialog(activity)
        mStorage = FirebaseStorage.getInstance().reference.child("posting")
        mAuth = FirebaseAuth.getInstance()
        mUserId = mAuth!!.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance().reference
        rv_cerita = root.find(R.id.rv_cerita)
           btn_kamera = root.find(R.id.btn_kamera)
        btn_close = root.find(R.id.btn_closefoto)
        btn_check = root.find(R.id.btn_check)
        rv_cerita.visibility = View.VISIBLE
        edit_cerita = root.find(R.id.edt_cerita)
        btn_check.setOnClickListener {
         when{
             filePath ==null-> toast("ambil gambar dulu")
             else ->{
                 progressDialog.setTitle("Sedang Upload")
                 progressDialog.show()
                 val bitmap = (img_tampilkamera.drawable as BitmapDrawable).bitmap
                 val baos = ByteArrayOutputStream()
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                 val data = baos.toByteArray()
                 img_tampilkamera.isDrawingCacheEnabled = true
                 img_tampilkamera.buildDrawingCache()
                 val user_id = mAuth!!.currentUser!!.uid
                 val fileref = mStorage!!.child(System.currentTimeMillis().toString() +".jpg") //filerife
                 var uploadTask: StorageTask<*>
                 uploadTask = fileref.putBytes(data)
                 uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                     if (!task.isSuccessful) {
                         task.exception?.let {
                             throw  it

                         }
                     }
                     return@Continuation fileref.downloadUrl
                 }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                     if (task.isSuccessful) {
                         val downloadUrl = task.result
                         myUrl = downloadUrl.toString()
                         val c = Calendar.getInstance()
                         val year = c.get(Calendar.YEAR)
                         val month = c.get(Calendar.MONTH)
                         val day = c.get(Calendar.DAY_OF_MONTH)
                         val key =
                             FirebaseDatabase.getInstance().reference.push().key

                         val usermap:MutableMap<String,Any?> = HashMap()
                         usermap["image"] = myUrl
                         usermap["cerita"] = edit_cerita.text.toString()
                         usermap["email"] = sessionManager.getemail().toString()
                         usermap["name"] = sessionManager.getnama().toString()
                         usermap["foto"] = sessionManager.getfoto().toString()
                         usermap["namalokasi"] = sessionManager.getlokasi().toString()
                         usermap["latitude"] = sessionManager.getlatitude().toString()
                         usermap["longitude"] = sessionManager.getlongitude().toString()
                         usermap["kalender"] = sessionManager.getkalender().toString()
                         usermap["year"] = year.toString()
                         usermap["month"] = month.toString()
                         usermap["day"] = day.toString()

                         mDatabase!!.child("posting").child(key!!).setValue(usermap)
                             .addOnCompleteListener(OnCompleteListener<Void?> {
                                 activity?.finish()
                                 startActivity<bottonNavigation>()
                             })
                         toast("upload sukses")
                         progressDialog.dismiss()
                     } else {
                         toast("gagal upload")
                         progressDialog.dismiss()
                        }
                 })
             }
         }
        }
        btn_close.setOnClickListener {
            activity?.finish()
        }
        btn_kamera.setOnClickListener {

            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i,123)

        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==123 && resultCode == Activity.RESULT_OK){
             filePath  = data!!.extras!!.get("data").toString()
            var bmp=data.extras!!.get("data") as Bitmap
            img_tampilkamera.setImageBitmap(bmp)

        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            filePath = null
            getFragmentManager()?.beginTransaction()?.detach(this)?.attach(this)?.commit();
        }
    }


}