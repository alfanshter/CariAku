package com.alfanshter.aplikasiiska.home.ui.MenuUpload

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.Utils.GpsUtils
import com.alfanshter.aplikasiiska.home.bottonNavigation
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class DetailFotoActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mStorage: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var myUrl = ""
    private var mUserId: String? = null
    private var mDatabase: DatabaseReference? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    private val locationRequestCode = 1000
    private var wayLatitude = 0.0
    private  var wayLongitude:kotlin.Double = 0.0
    lateinit var locationRequest : LocationRequest
    lateinit var locationCallback : LocationCallback
    private var isContinue = false
    private var isGPS = false
    private var LOCATION_REQUEST = 1000
    private var GPS_REQUEST = 1001
    var result: String? = null
    lateinit var address : Address
    lateinit var sb : java.lang.StringBuilder
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_foto)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000.toLong() // 10 seconds

        locationRequest.fastestInterval = 5 * 1000.toLong() // 5 seconds
        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })
        if (!isGPS) {
            Toast.makeText(this, "Nyalakan GPS", Toast.LENGTH_SHORT).show()
            return
        }
        isContinue = false
        getLocation()

        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        mStorage = FirebaseStorage.getInstance().reference.child("posting")
        mAuth = FirebaseAuth.getInstance()
        mUserId = mAuth!!.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance().reference
        var bundle :Bundle ?=intent.extras
        var message = bundle!!.getString("hei") // 1
//        val imageBytes = Base64.decode(message, 0)
//        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
           gambarbbaru.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(message),700,
               650,false))

            btn_close.setOnClickListener {
                startActivity<bottonNavigation>()
            }

            checkOK.setOnClickListener {
                val bitmap = (gambarbbaru.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                gambarbbaru.isDrawingCacheEnabled = true
                gambarbbaru.buildDrawingCache()

                progressDialog.setTitle("Sedang Upload")
                progressDialog.show()
                val user_id = mAuth!!.currentUser!!.uid
                val fileref = mStorage!!.child(System.currentTimeMillis().toString() +".jpg") //filerife
                var uploadTask: StorageTask<*>
                uploadTask = fileref.putBytes(data)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw  it
                            progressDialog.dismiss()

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
                        usermap["cerita"] = edt_cerita.text.toString()
                        usermap["email"] = sessionManager.getemail().toString()
                        usermap["name"] = sessionManager.getnama().toString()
                        usermap["foto"] = sessionManager.getfoto().toString()
                        usermap["latitude"] = wayLatitude.toString()
                        usermap["longitude"] = wayLongitude.toString()
                        usermap["namalokasi"] = result.toString()
                        usermap["uid"] = user_id.toString()
                        usermap["kalender"] = sessionManager.getkalender().toString()
                        usermap["year"] = year.toString()
                        usermap["month"] = month.toString()
                        usermap["day"] = day.toString()
                        toast(wayLatitude.toString())
                        mDatabase!!.child("posting").child(key!!).setValue(usermap)
                            .addOnCompleteListener(OnCompleteListener<Void?> {
                                progressDialog.dismiss()
                                finish()
                                startActivity<bottonNavigation>()
                            })
                        progressDialog.dismiss()
                        toast("upload sukses")
                    } else {
                        toast("gagal upload")
                        progressDialog.dismiss()
                    }
                })
            }

    }


    private fun getLocation() {
        var geocoder: Geocoder
        var addressList = ArrayList<Address>()
        geocoder = Geocoder(applicationContext, Locale.getDefault())

        if (ActivityCompat.checkSelfPermission(
                this@DetailFotoActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@DetailFotoActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DetailFotoActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                mFusedLocationClient.lastLocation
                    .addOnSuccessListener(this@DetailFotoActivity) { location ->
                        if (location != null) {
                            wayLatitude = location.getLatitude()
                            wayLongitude = location.getLongitude()
                            addressList = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as ArrayList<Address>
                            if (addressList != null && addressList.size > 0) {
                                address = addressList[0]
                                 sb = StringBuilder()
                                for (i in 0 until address.maxAddressLineIndex) {
                                    sb.append(address.getAddressLine(i)).append("\n")
                                }
                                sb.append(address.locality).append(",")
                                sb.append(address.postalCode).append(",")
                                sb.append(address.countryName)
                                result = sb.toString()
                            }
                            val message = Message.obtain()
                            if (result != null) {
                                message.what = 1
                                val bundle = Bundle()
                                bundle.putString("address", result)
                                message.data = bundle
                            }
                           lokasi.text =result
                        } else {
                            mFusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            )
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size> 0
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    mFusedLocationClient.lastLocation.addOnSuccessListener(
                        this
                    ) { location: Location? ->
                        if (location != null) {
                            wayLatitude = location.latitude
                            wayLongitude = location.longitude
                            lokasi.setText(
                                java.lang.String.format(
                                    Locale.US,
                                    "%s -- %s",
                                    wayLatitude,
                                    wayLongitude
                                )
                            )
                        }
                        else{
                            lokasi.text = "error"
                        }
                    }
                } else {
                    Toast.makeText(this, "aktifkan lokasi terlebih dahulu", Toast.LENGTH_SHORT).show()
                    startActivity<bottonNavigation>()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }
}