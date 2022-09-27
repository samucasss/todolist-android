package com.samuca.todolist.rest

import com.samuca.todolist.model.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioRest {

    @Headers("Content-Type: application/json")
    @POST("/usuario")
    fun save(@Body usuario: Usuario, @Header("Authorization") auth: String) : Call<Usuario>

    @DELETE("/usuario")
    fun delete(@Header("Authorization") auth: String) : Call<String>

}