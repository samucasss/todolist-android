package com.samuca.todolist.rest

import com.samuca.todolist.auth.User
import com.samuca.todolist.model.Login
import com.samuca.todolist.model.Token
import com.samuca.todolist.model.Usuario
import retrofit2.Call
import retrofit2.http.*

interface AuthRest {

    @Headers("Content-Type: application/json")
    @POST("/auth/register")
    fun register(@Body usuario: Usuario) : Call<Usuario>

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    fun login(@Body login: Login): Call<Token>

    @GET("/auth/user")
    fun get(@Header("Authorization") auth: String): Call<User>
}