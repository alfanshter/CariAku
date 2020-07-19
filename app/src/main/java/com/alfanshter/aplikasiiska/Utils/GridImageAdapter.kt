package com.alfanshter.aplikasiiska.Utils

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.alfanshter.aplikasiiska.R
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import java.util.*


class GridImageAdapter(
    context: Context,
    layoutResource: Int,
    append: String,
    imgURLs: ArrayList<String>
) :
    ArrayAdapter<String?>(context, layoutResource, imgURLs as List<String?>) {
    private val mContext: Context
    private val mInflater: LayoutInflater
    private val layoutResource: Int
    private val mAppend: String
    private val imgURLs: ArrayList<String>

    private class ViewHolder {
        var image: SquareImageView? = null
        var mProgressBar: ProgressBar? = null
    }

    @NonNull
    override fun getView(
        position: Int,
        @Nullable convertView: View?,
        @NonNull parent: ViewGroup
    ): View {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false)
            holder = ViewHolder()
            holder.mProgressBar =
                convertView.findViewById<View>(R.id.gridImageProgressbar) as ProgressBar
            holder.image =
                convertView.findViewById<View>(R.id.gridImageView) as SquareImageView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val imgURL = getItem(position)
        val imageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(mAppend + imgURL, holder.image, object : ImageLoadingListener {
            override fun onLoadingStarted(
                imageUri: String,
                view: View
            ) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar!!.visibility = View.VISIBLE
                }
            }

            override fun onLoadingFailed(
                imageUri: String,
                view: View,
                failReason: FailReason
            ) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar!!.visibility = View.GONE
                }
            }

            override fun onLoadingComplete(
                imageUri: String,
                view: View,
                loadedImage: Bitmap
            ) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar!!.visibility = View.GONE
                }
            }

            override fun onLoadingCancelled(
                imageUri: String,
                view: View
            ) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar!!.visibility = View.GONE
                }
            }
        })
        return convertView!!
    }

    init {
        mInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        this.layoutResource = layoutResource
        mAppend = append
        this.imgURLs = imgURLs
    }
}



















