package com.alfanshter.aplikasiiska.home.ui.Maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alfanshter.aplikasiiska.Model.AmbilData
import com.alfanshter.aplikasiiska.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import org.jetbrains.anko.AnkoLogger
import java.util.*


class MapsFragment : Fragment(),AnkoLogger {

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    lateinit var databaseReference : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        mMapView = root.findViewById(R.id.mapView)
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume() // needed to get the map to display immediately


        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView!!.getMapAsync { mMap ->
            googleMap = mMap
            var latitude : String
            var longitude : String
            databaseReference =
                FirebaseDatabase.getInstance().reference.child("posting")
            databaseReference.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (child in p0.getChildren()) {
                        var data = child.getValue(AmbilData::class.java)
                        var hari = data!!.day.toString().toInt()
                        var bulan = data.month.toString().toInt()
                        var tahun = data.year.toString().toInt()
                         latitude = data.latitude.toString()
                         longitude = data.longitude.toString()
                        var nama = data.name.toString()
                        var  email = data.email.toString()
                        var foto = data.foto.toString()
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        if (tahun == year && bulan == month && hari == day){
                            var marker: LatLng? =
                                LatLng(latitude.toDouble(), longitude.toDouble())
                            googleMap!!.addMarker(
                                MarkerOptions().position(marker!!).title(nama)
                                    .snippet(email)
                            )
                            val cameraPosition =
                                CameraPosition.Builder().target(LatLng(latitude.toDouble(),longitude.toDouble())).zoom(12f).build()
                            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }


                    }

                }

            })
            // For showing a move to my location button

            // For dropping a marker at a point on the Map

            // For zooming automatically to the location of the marker


        }
        return root
    }



    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }
}