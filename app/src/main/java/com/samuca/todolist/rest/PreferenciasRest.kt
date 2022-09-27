package com.samuca.todolist.rest

import com.samuca.todolist.model.Preferencias
import retrofit2.Call
import retrofit2.http.*

interface PreferenciasRest {

    @GET("/preferencia")
    fun get(@Header("Authorization") auth: String): Call<Preferencias>

    @Headers("Content-Type: application/json")
    @POST("/preferencias")
    fun save(@Body preferencias: Preferencias, @Header("Authorization") auth: String) : Call<Preferencias>

    @DELETE("/preferencia")
    fun delete(@Header("Authorization") auth: String) : Call<String>

}