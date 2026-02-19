package com.example.aepbill.data.model

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    val relay: Int,
    val relay2: Int,
    val relay3: Int,
    val relay4: Int,
    val current: Float,
    val time: String,
    @SerializedName("next_alarm") val nextAlarm: String,
    @SerializedName("alarm_active") val alarmActive: Boolean,
    @SerializedName("anomaly_code") val anomalyCode: Int,
    val heap: Long,
    @SerializedName("ota_mode") val otaMode: Boolean = false
)

data class Thresholds(
    @SerializedName("min_load") val minLoad: Float,
    val anomaly: Float,
    @SerializedName("max_critical") val maxCritical: Float
)

data class SettingsResponse(
    @SerializedName("wifi_ssid") val wifiSsid: String,
    val timezone: String,
    val thresholds: Thresholds,
    @SerializedName("scale_factor") val scaleFactor: Float
)

data class ActionResponse(
    val status: String,
    @SerializedName("wifi_restart_required") val wifiRestartRequired: Boolean? = null
)

data class WifiNetwork(
    val ssid: String,
    val rssi: Int,
    val authmode: Int
)

data class WifiScanResponse(
    val networks: List<WifiNetwork>
)
