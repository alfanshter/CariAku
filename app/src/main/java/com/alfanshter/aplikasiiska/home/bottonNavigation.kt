package com.alfanshter.aplikasiiska.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Message
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.alfanshter.aplikasiiska.Model.AmbilData
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.Utils.GpsUtils
import com.alfanshter.aplikasiiska.auth.LoginActivity
import com.alfanshter.aplikasiiska.home.ui.MenuUpload.MenuUpload
import com.alfanshter.aplikasiiska.home.ui.dashboard.DashboardFragment
import com.alfanshter.aplikasiiska.home.ui.Maps.MapsFragment
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_botton_navigation.*
import kotlinx.android.synthetic.main.activity_detail_foto.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import java.util.*

class bottonNavigation : AppCompatActivity(),AnkoLogger {
lateinit var dbes: DatabaseReference
    lateinit var session:SessionManager
    lateinit var akun : FirebaseAuth
    lateinit var uid : String
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
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame, DashboardFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                startActivity<MenuUpload>()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame, MapsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }

        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_botton_navigation)
        session = SessionManager(this)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val kalender = "$year/$month/$day"
        session.setkalender(kalender)

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

         akun = FirebaseAuth.getInstance()
        uid = akun.currentUser!!.uid

        dbes = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
        dbes.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              var getdata = p0.getValue(AmbilData::class.java)
                var email = getdata!!.email.toString()
                var nama = getdata.name.toString()
                var foto = getdata.foto.toString()
                val navigationview = findViewById<NavigationView>(R.id.navigationView)
                val headerview = navigationview.getHeaderView(0)
                val placeholderOption =
                    RequestOptions()
                val navname = headerview.findViewById<TextView>(R.id.nama_drawer)
                val navemail = headerview.findViewById<TextView>(R.id.email_drawer)

                val navgambar = headerview.findViewById<ImageView>(R.id.gambardrawer)
                navname.text = nama
                navemail.text = email
                Glide.with(container.context).setDefaultRequestOptions(placeholderOption).load(foto).into(navgambar)

                session.setnama(nama)
                session.setemail(email)
                session.setfoto(foto)

              }

        })
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        drawerlayout()

        moveToFragment(DashboardFragment())
    }
    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frame, fragment)
        fragmentTrans.commit()
    }

    fun drawerlayout()
    {
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            container,
            dashboar_toolbar,
           R.string.drawer_open,
           R.string.drawer_close
        ){

        }

        drawerToggle.isDrawerIndicatorEnabled = true
        container.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_logout -> {
                    akun.signOut()
                    session.setLogin(false)
                    startActivity<LoginActivity>()
                    finish()

                }
            }
            container.closeDrawer(GravityCompat.START)
            true

        }

        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


    }

    private fun getLocation() {
        var geocoder: Geocoder
        var addressList = ArrayList<Address>()
        geocoder = Geocoder(applicationContext, Locale.getDefault())

        if (ActivityCompat.checkSelfPermission(
                this@bottonNavigation,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@bottonNavigation,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@bottonNavigation,
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
                    .addOnSuccessListener(this@bottonNavigation) { location ->
                        if (location != null) {
                            wayLatitude = location.getLatitude()
                            wayLongitude = location.getLongitude()
                            session.setlatitude(wayLatitude.toString())
                            session.setlongitude(wayLongitude.toString())
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
                            session.setlokasi(result)
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
