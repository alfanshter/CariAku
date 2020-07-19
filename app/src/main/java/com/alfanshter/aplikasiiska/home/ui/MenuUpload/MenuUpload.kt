package com.alfanshter.aplikasiiska.home.ui.MenuUpload

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.Session.Permissions
import com.alfanshter.aplikasiiska.home.ui.MenuUpload.foto.MyPagerAdapter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.esafirm.rximagepicker.RxImagePicker
import kotlinx.android.synthetic.main.activity_menu_upload.*
import rx.Observable


class MenuUpload : AppCompatActivity() {
    private val ACTIVITY_NUM = 2
    private val VERIFY_PERMISSIONS_REQUEST = 1
    private val TAG = "MenuUpload"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_upload)

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }


        val fragmentadapter = MyPagerAdapter(supportFragmentManager)
        viewpager.adapter = fragmentadapter

        tablayout.setupWithViewPager(viewpager)
    }

    fun verifyPermissions(permissions: Array<String>) {
        Log.d(TAG, "verifyPermissions: verifying permissions.")
        ActivityCompat.requestPermissions(
            this@MenuUpload,
            permissions!!,
            VERIFY_PERMISSIONS_REQUEST
        )
    }
    fun checkPermissionsArray(permissions: Array<String>): Boolean {
        for (i in permissions.indices) {
            val check = permissions[i]
            if (!checkPermissions(check!!)) {
                return false
            }
        }
        return true
    }

    fun checkPermissions(permission: String): Boolean {
        Log.d(TAG, "checkPermissions: checking permission: $permission")
        val permissionRequest =
            ActivityCompat.checkSelfPermission(this@MenuUpload, permission)
        return if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(
                TAG,
                "checkPermissions: \n Permission was not granted for: $permission"
            )
            false
        } else {
            Log.d(
                TAG,
                "checkPermissions: \n Permission was granted for: $permission"
            )
            true
        }
    }
    private val imagePickerObservable: Observable<List<Image>>
        private get() = RxImagePicker.getInstance()
            .start(this, ImagePicker.create(this))// max images can be selected (99 by default)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_CAMERA) {
            if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun captureImage() {
        ImagePicker.cameraOnly().start(this)
    }

    companion object {
        private const val RC_CAMERA = 3000
    }
}