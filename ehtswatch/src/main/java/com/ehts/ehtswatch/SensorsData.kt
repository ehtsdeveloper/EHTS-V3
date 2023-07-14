package com.ehts.ehtswatch


data class SensorsData(
    var resting: Double? = 0.0,
    var low: Double? = 0.0,
    var max: Double? = 0.0,
   // var recordingTimeStamp: String? = null,
    val recordingStartTime: String? = null,
    val recordingStopTime: String? = null,
    var key: String? = null
)