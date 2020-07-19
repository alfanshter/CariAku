package com.alfanshter.aplikasiiska.Model

class AmbilData {

    /// MOdel class
    var name: String? = null
    var email: String? = null
    var foto: String? = null
    var nomor: String? = null
    var password: String? = null
    var cerita: String? = null
    var image: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var namalokasi: String? = null
    var kalender :String? = null
    var day:String? = null
    var month:String? = null
    var year:String? = null
    constructor() {

    }

    constructor(
        email: String?,
        name: String?,
        nomor: String?,
        foto: String?,
        password: String?,
        cerita: String?,
        image: String?,
        latitude: String?,
        longitude: String?,
        namalokasi: String?,
        kalender:String?,
        day:String?,
        month:String?,
        year:String?
    ) {
        this.name = name
        this.email = email
        this.foto = foto
        this.nomor = nomor
        this.password = password
        this.cerita = cerita
        this.image = image
        this.latitude = latitude
        this.longitude = longitude
        this.namalokasi = namalokasi
        this.kalender = kalender
        this.day = day
        this.month = month
        this.year = year
    }
}