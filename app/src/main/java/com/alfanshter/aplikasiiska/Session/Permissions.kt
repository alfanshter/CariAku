package com.alfanshter.aplikasiiska.Session

import android.Manifest


object Permissions {
    val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    val CAMERA_PERMISSION = arrayOf(
        Manifest.permission.CAMERA
    )

    val READ_STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}
