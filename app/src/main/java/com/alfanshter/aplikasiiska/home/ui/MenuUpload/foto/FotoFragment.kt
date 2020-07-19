package com.alfanshter.aplikasiiska.home.ui.MenuUpload.foto

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.home.ui.MenuUpload.DetailFotoActivity
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.features.cameraonly.CameraOnlyConfig
import com.esafirm.imagepicker.helper.IpLogger
import com.esafirm.imagepicker.model.Image
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


class FotoFragment : Fragment() {
    private val NUM_GRID_COLUMNS = 3
    private lateinit var btn_close : ImageButton
    private var photoPreview: ImageView? = null
    private lateinit var btn_check : ImageButton
    private var imagePickerFragment: ImagePickerFragment? = null
    private var cameraOnlyConfig: CameraOnlyConfig? = null
    private var config: ImagePickerConfig? = null
     var logic : Bitmap?=null
    val imagePicker : ImagePicker
        get(){
            val imagePicker = com.esafirm.imagepicker.features.ImagePicker.create(activity)
                .language("in") // Set image picker language
                .theme(R.style.ImagePickerTheme)
                .returnMode(if (true) ReturnMode.ALL else ReturnMode.NONE) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                .folderMode(false) // set folder mode (false by default)
                .includeVideo(false) // include video (false by default)
                .onlyVideo(false) // include video (false by default)
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE") // done button text
            ImagePickerComponentHolder.getInstance().imageLoader
            imagePicker.single()
            return imagePicker.limit(10) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // captured image directory name ("Camera" folder by default)
                .imageFullDirectory(
                    Environment.getExternalStorageDirectory().path
                ) // can be full path
        }

    val baru = imagePicker.config

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        config = baru
        activity?.setTheme(config!!.theme)
        val root = inflater.inflate(R.layout.fragment_foto, container, false)
        photoPreview = root.find(R.id.photo_preview)
        btn_check = root.find(R.id.checkOK)
        btn_check.visibility = View.INVISIBLE

        if (savedInstanceState != null) {
            // The fragment has been restored.
            IpLogger.getInstance().e("Fragment has been restored")
            imagePickerFragment =
                activity?.supportFragmentManager!!.findFragmentById(R.id.ef_imagepicker_fragment_placeholder) as ImagePickerFragment?
        }
        else {
            IpLogger.getInstance().e("Making fragment")
            imagePickerFragment = ImagePickerFragment.newInstance(config, cameraOnlyConfig)
            val ft =
                activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.ef_imagepicker_fragment_placeholder, imagePickerFragment!!)
            ft?.commit()
        }
        imagePickerFragment?.setInteractionListener(CustomInteractionListener())

        btn_close = root.find(R.id.btn_close)
        btn_close.setOnClickListener {
            activity?.finish()
        }

        return root
    }



    internal inner class CustomInteractionListener :
        ImagePickerInteractionListener {
        override fun setTitle(title: String) {
        }

        override fun cancel() {
            activity?.finish()
        }
        override fun selectionChanged(imageList: List<Image>) {
            if (imageList.isEmpty()) {
                photoPreview?.setImageDrawable(null)
                btn_check.visibility = View.INVISIBLE
            } else {
                photoPreview?.setImageBitmap(
                    BitmapFactory.decodeFile(
                        imageList[imageList.size - 1].path
                    )
                )
                btn_check.visibility = View.VISIBLE
                btn_check.setOnClickListener {

                    startActivity(intentFor<DetailFotoActivity>("hei" to imageList[imageList.size -1].path.toString()))
                }
            }
        }

        override fun finishPickImages(result: Intent) {

        }
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            getFragmentManager()?.beginTransaction()?.detach(this)?.attach(this)?.commit();
        }
    }

}