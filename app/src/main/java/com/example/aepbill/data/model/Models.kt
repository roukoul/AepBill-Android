package com.example.aepbill.data.model

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    val health: Float,
    val temp: Float,
    val status: String,
    @SerializedName("days_remaining") val daysRemaining: Int? = null
)

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
    @SerializedName("aps") val networks: List<WifiNetwork>
)

data class PowerResponse(
    val mode: Int,
    @SerializedName("start_hour") val startHour: Int,
    @SerializedName("start_min") val startMin: Int,
    @SerializedName("end_hour") val endHour: Int,
    @SerializedName("end_min") val endMin: Int,
    @SerializedName("start_sec") val startSec: Int? = null,
    @SerializedName("end_sec") val endSec: Int? = null,
    @SerializedName("start_day") val startDay: Int? = null,
    @SerializedName("start_month") val startMonth: Int? = null,
    @SerializedName("start_year") val startYear: Int? = null,
    @SerializedName("end_day") val endDay: Int? = null,
    @SerializedName("end_month") val endMonth: Int? = null,
    @SerializedName("end_year") val endYear: Int? = null
)
