package com.example.kotlinviewmodel.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://meutcc.local"
private const val URL_2 = "http://192.168.18.36" // This URL is not currently used in the Retrofit builder

private val inter = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL) // Ensure this is the correct base URL for your microcontrolller
    .build()

interface conection {
    @GET("pisca")
    suspend fun piscaLed(
        @Query("tempo") tempo:Int
    ): Response<String>

    @GET("battery")
    suspend fun verificarBateria(): Response<String>

    @GET("setAlarm") // New endpoint for setting alarms
    suspend fun setAlarm(
        @Query("nome") nomeAlarme: String,
        @Query("ativo") ativo: Boolean,
        @Query("dias") diasSemana: String
    ): Response<String>
}

object IntApi{
    val intService : conection by lazy {
        inter.create(conection::class.java)
    }
}   