package com.samuca.todolist.rest

import com.samuca.todolist.model.Tecnologia
import retrofit2.Call
import retrofit2.http.GET

interface TecnologiaRest {

    @GET("tecnologias")
    fun findAll() : Call<List<Tecnologia>>
}