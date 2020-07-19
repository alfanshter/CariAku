package com.alfanshter.udinlelangfix.Session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context?) {
    val privateMode = 0
    val privateName ="login"
    var Pref : SharedPreferences?=context?.getSharedPreferences(privateName,privateMode)
    var editor : SharedPreferences.Editor?=Pref?.edit()

    private val islogin = "login"
    fun setLogin(check: Boolean){
        editor?.putBoolean(islogin,check)
        editor?.commit()
    }

    fun getLogin():Boolean?
    {
        return Pref?.getBoolean(islogin,false)
    }

    private val email = "email"
    fun setemail(check:String?)
    {
        editor?.putString(email,check)
        editor?.commit()
    }

    fun getemail():String?
    {
        return Pref?.getString(email,"")
    }

    private val nama = "nama"
    fun setnama(check:String?)
    {
        editor?.putString(nama,check)
        editor?.commit()
    }

    fun getnama():String?
    {
        return Pref?.getString(nama,"")
    }

    private val foto = "foto"
    fun setfoto(check:String?)
    {
        editor?.putString(foto,check)
        editor?.commit()
    }

    fun getfoto():String?
    {
        return Pref?.getString(foto,"")
    }

    private val lokasi = "lokasi"
    fun setlokasi(check:String?)
    {
        editor?.putString(lokasi,check)
        editor?.commit()
    }

    fun getlokasi():String?
    {
        return Pref?.getString(lokasi,"")
    }

    private val latitude = "latitude"
    fun setlatitude(check:String?)
    {
        editor?.putString(latitude,check)
        editor?.commit()
    }

    fun getlatitude():String?
    {
        return Pref?.getString(latitude,"")
    }

    private val longitude = "longitude"
    fun setlongitude(check:String?)
    {
        editor?.putString(longitude,check)
        editor?.commit()
    }

    fun getlongitude():String?
    {
        return Pref?.getString(longitude,"")
    }

    private val kalender = "kalender"
    fun setkalender(check:String?)
    {
        editor?.putString(kalender,check)
        editor?.commit()
    }

    fun getkalender():String?
    {
        return Pref?.getString(kalender,"")
    }


}