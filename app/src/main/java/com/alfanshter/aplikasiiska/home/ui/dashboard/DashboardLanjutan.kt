package com.alfanshter.aplikasiiska.home.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanshter.aplikasiiska.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_botton_navigation.*
import kotlinx.android.synthetic.main.activity_dashboard_lanjutan.*
import org.jetbrains.anko.toast

class DashboardLanjutan : AppCompatActivity() {
    var latitude: String? = null
    private var googleMap: GoogleMap? = null
    lateinit var foto : String
    lateinit var nama : String
    lateinit var image : String
    lateinit var cerita : String
    lateinit var lokasi : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_lanjutan)
        var bundle :Bundle ?=intent.extras
        var latitude = bundle!!.getString("latitude") // 1
        var longitude = bundle.getString("longitude") // 2
         nama = bundle.getString("name").toString()
        image = bundle.getString("image").toString()
         foto = bundle.getString("foto").toString()
         cerita = bundle.getString("cerita").toString()
        lokasi = bundle.getString("lokasi").toString()
        mapView.onCreate(savedInstanceState)
        mapView.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync { mMap ->
            googleMap = mMap

            // For showing a move to my location button

            // For dropping a marker at a point on the Map
            var sydney: LatLng? =
                LatLng(latitude!!.toDouble(),longitude!!.toDouble())
            googleMap!!.addMarker(
                MarkerOptions().position(sydney!!).title(nama)
            )

            // For zooming automatically to the location of the marker
            val cameraPosition =
                CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        data()

    }

    fun data(){
        val placeholderOption =
            RequestOptions()
        Glide.with(this)
            .setDefaultRequestOptions(placeholderOption).load(foto)
            .into(img_foto)
        txt_nama.text = nama
        Picasso.get().load(image).fit().centerCrop().into(img_posting)
        txt_cerita.text = cerita
        txt_lokasi.text = lokasi

    }
}