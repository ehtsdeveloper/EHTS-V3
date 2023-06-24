package com.ehts.ehtswatch


data class ProfileData(
    var dataName: String? = null,
    var dataEmpID: String? = null,
   // var dataAge: Int = 0,
    //var dataHeight: Int = 0,
    //var dataWeight: Int = 0,
    var dataDeviceID: String? = null,
    //var dataImage: String? = null,
    var key: String? = null
) {
    constructor() : this(null, null, null, null)
}