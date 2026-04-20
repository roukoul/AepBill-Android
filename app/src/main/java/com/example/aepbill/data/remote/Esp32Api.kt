package com.example.aepbill.data.remote

import com.example.aepbill.data.model.ActionResponse
import com.example.aepbill.data.model.ScheduleResponse
import com.example.aepbill.data.model.SettingsResponse
import com.example.aepbill.data.model.StatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Esp32Api {
    @GET("status")
    suspend fun getStatus(): Response<StatusResponse>

    @GET("api/health")
    suspend fun getHealth(): Response<com.example.aepbill.data.model.HealthResponse>

    @GET("wifi_scan")
    suspend fun getWifiScan(): Response<com.example.aepbill.data.model.WifiScanResponse>

    @GET("api/settings")
    suspend fun getSettings(): Response<SettingsResponse>

    @GET("api/schedule")
    suspend fun getSchedule(): Response<ScheduleResponse>

    @POST("api/settings")
    suspend fun updateSettings(@Body settings: SettingsResponse): Response<Void>

    @GET("api/power")
    suspend fun getPower(): Response<com.example.aepbill.data.model.PowerResponse>

    @POST("api/power")
    suspend fun updatePower(@Body power: com.example.aepbill.data.model.PowerResponse): Response<Void>

    @GET("restart")
    suspend fun restart(@Query("confirm") confirm: String = "yes"): Response<Void>

    @GET("restart")
    suspend fun restartSystem(@Query("confirm") confirm: String = "yes"): Response<Unit>
    
    @GET("recalibrateZero")
    suspend fun recalibrateZero(): Response<Unit>

    @GET("factoryReset")
    suspend fun factoryReset(@Query("confirm") confirm: String = "yes"): Response<Void>
}
