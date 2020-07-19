@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.alfanshter.aplikasiiska.Utils

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import java.io.File

object FileSearch {
    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    fun getDirectoryPaths(directory: String?): ArrayList<String>? {
        val pathArray: ArrayList<String> = ArrayList()
        val file = File(directory)
        Log.v(TAG,"file ${file.exists()}")
        Log.v(TAG,"file ${file.isDirectory()}")
        val listfiles = file.listFiles()
        Log.v(TAG,"file ${file.listFiles()}")
        for (i in listfiles.indices) {
            if (listfiles[i].isDirectory) {
                pathArray.add(listfiles[i].absolutePath)
            }
        }
        return pathArray
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    fun getFilePaths(directory: String?): ArrayList<String> {
        val pathArray: ArrayList<String> = ArrayList()
        val file = File(directory)
        val listfiles = file.listFiles()
        for (i in listfiles.indices) {
            if (listfiles[i].isFile) {
                pathArray.add(listfiles[i].absolutePath)
            }
        }
        return pathArray
    }
}