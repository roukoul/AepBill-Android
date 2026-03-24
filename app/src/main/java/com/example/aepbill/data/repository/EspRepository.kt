package com.example.aepbill.data.repository

import com.example.aepbill.data.model.ScheduleResponse
import com.example.aepbill.data.model.SettingsResponse
import com.example.aepbill.data.model.Status
import com.example.aepbill.data.remote.Esp32Api

class EspRepository(
    private val api: Esp32Api
) {
    suspend fun getStatus(): Status {
        val response = api.getStatus()
        if (response.isSuccessful && response.body() != null) {
            val raw = response.body()!!
            return Status(
                relay = raw.relay == 1,
                relay2 = raw.relay2 == 1,
                relay3 = raw.relay3 == 1,
                relay4 = raw.relay4 == 1,
                current = raw.current,
                time = raw.time,
                wifiMode = "STA", // Default to STA as it's not in response yet
                alarmsEnabled = raw.alarmActive,
                nextAlarm = raw.nextAlarm,
                anomalyCode = raw.anomalyCode
            )
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }

    suspend fun getHealth(): com.example.aepbill.data.model.HealthResponse? {
        return try {
            val response = api.getHealth()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWifiScan(): Result<com.example.aepbill.data.model.WifiScanResponse> {
        return try {
            val response = api.getWifiScan()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error scanning WiFi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSettings(): Result<SettingsResponse> {
        return try {
            val response = api.getSettings()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error loading settings"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSchedule(): Result<ScheduleResponse> {
        return try {
            val response = api.getSchedule()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error loading schedule"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSettings(settings: SettingsResponse): Result<Boolean> {
        return try {
            val response = api.updateSettings(settings)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restartDevice(): Result<Boolean> {
        return try {
            val response = api.restart()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Restart failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun factoryReset(): Result<Boolean> {
        return try {
            val response = api.factoryReset()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Factory Reset failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
