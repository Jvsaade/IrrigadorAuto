package com.example.kotlinviewmodel.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory // Importar GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST // Importar POST
import retrofit2.http.Query
import retrofit2.http.Body // Importar Body
import com.example.kotlinviewmodel.baseDados.Configuration // Importar a classe Configuration

private const val BASE_URL = "http://meutcc.local" // Certifique-se de que esta é a URL correta do seu microcontrolador
private const val URL_2 = "http://192.168.18.36" // Esta URL não está sendo usada no construtor Retrofit atualmente

private val inter = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create()) // Adicionar o conversor Gson
    .baseUrl(BASE_URL)
    .build()

interface conection {
    @GET("pisca")
    suspend fun piscaLed(
        @Query("tempo") tempo:Int
    ): Response<String>

    @GET("battery")
    suspend fun verificarBateria(): Response<String>

    @POST("setAlarm") // Novo endpoint para enviar JSON (mude o nome se preferir)
    suspend fun setAlarm(
        @Body alarm: Configuration // Envia o objeto Configuration como corpo da requisição JSON
    ): Response<String>

    @POST("editAlarm") // Novo endpoint para enviar JSON (mude o nome se preferir)
    suspend fun editAlarm(
        @Query("nomeAntigo") nomeAntigo: String,
        @Body alarm: Configuration // Envia o objeto Configuration como corpo da requisição JSON
    ): Response<String>

    @POST("deleteAlarm")
    suspend fun deleteAlarm(
        @Query("name") name: String
    ): Response<String>

    @POST("activateAlarm")
    suspend fun activateAlarm(
        @Query("name") name: String
    ): Response<String>

    @POST("deactivateAlarm")
    suspend fun deactivateAlarm(
        @Query("name") name: String
    ): Response<String>

    @POST("deleteAll") // Deleta todos os alarmes
    suspend fun deleteAll(): Response<String>

    @GET("consultAlarm") // Verifica a existência do alarme
    suspend fun consultAlarm(
        @Query("name") nomeAlarme: String
    ): Response<String>
}

object IntApi{
    val intService : conection by lazy {
        inter.create(conection::class.java)
    }
}