package com.alfanshter.aplikasiiska.Utils

import android.os.Environment

class FilePath {
    //"storage/emulated/0"
    var ROOT_DIR: String = Environment.getExternalStorageDirectory().getPath()
    var PICTURES = "$ROOT_DIR/Pictures"
    var CAMERA = "$ROOT_DIR/DCIM/camera"
}