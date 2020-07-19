package com.alfanshter.aplikasiiska.home.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanshter.aplikasiiska.Model.AmbilData
import com.alfanshter.aplikasiiska.R
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class DashboardFragment : Fragment(), AnkoLogger {
    private lateinit var recyclerView: RecyclerView
    lateinit var refinfo: DatabaseReference
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        sessionManager = SessionManager(context?.applicationContext)
        recyclerView = root.find(R.id.rcyle_dashobard)
        val LayoutManager = LinearLayoutManager(context?.applicationContext)
        LayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = LayoutManager
        refinfo = FirebaseDatabase.getInstance().reference.child("posting")
        val option =
            FirebaseRecyclerOptions.Builder<AmbilData>().setQuery(refinfo, AmbilData::class.java)
                .build()
        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<AmbilData, MyViewHolder>(option) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView = LayoutInflater.from(context?.applicationContext)
                        .inflate(R.layout.card_view, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: MyViewHolder,
                    position: Int,
                    model: AmbilData
                ) {
                    val refid = getRef(position).key.toString()
                    refinfo.child(refid).addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists())
                            {
                                var getdata = p0.getValue(AmbilData::class.java)
                                var nama = getdata!!.name.toString()
                                var foto = getdata.foto.toString()
                                var image = getdata.image.toString()
                                var cerita = getdata.cerita.toString()
                                var longitude = getdata.longitude.toString()
                                var latitude = getdata.latitude.toString()
                                var namalokasi = getdata.namalokasi.toString()
                                var kalender = getdata.kalender.toString()
                                val placeholderOption =
                                    RequestOptions()
                                Glide.with(container!!.context)
                                    .setDefaultRequestOptions(placeholderOption).load(foto)
                                    .into(holder.foto)
                                Glide.with(container.context)
                                    .setDefaultRequestOptions(placeholderOption).load(image)
                                    .fitCenter()
                                    .into(holder.gambarposting)
                                holder.cerita.text = "$nama $cerita"
                                holder.nama.text = nama
                                holder.lokasi.text = namalokasi
                                holder.tanggalan.text = kalender
                                holder.judul.text = nama
                                holder.itemView.setOnClickListener {
                                    startActivity<DashboardLanjutan>(
                                        "longitude" to longitude,
                                        "latitude" to latitude,
                                        "name" to nama,
                                        "foto" to foto,
                                        "cerita" to cerita,
                                        "image" to image,
                                        "kalender" to kalender,
                                        "lokasi" to namalokasi
                                    )
                                }
                            }


                        }

                    })
                }

            }

        recyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

        return root
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cerita: TextView = itemView.findViewById(R.id.txt_cerita)
        var nama: TextView = itemView.findViewById(R.id.txt_nama)
        var gambarposting: ImageView = itemView.findViewById(R.id.img_posting)
        var foto: CircleImageView = itemView.findViewById(R.id.img_foto)
        var lokasi: TextView = itemView.findViewById(R.id.txt_lokasi)
        var tanggalan : TextView = itemView.findViewById(R.id.txt_kalender)
        var judul : TextView = itemView.findViewById(R.id.txt_judul)
    }
}