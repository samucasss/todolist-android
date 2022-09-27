package com.samuca.todolist.rest

import com.samuca.todolist.model.Tarefa
import retrofit2.Call
import retrofit2.http.*
import java.time.LocalDate
import java.util.*

interface TarefaRest {

    @GET("/tarefas")
    fun findAll(@Query("inicio") inicio: String, @Query("fim") fim: String,
                @Header("Authorization") auth: String): Call<List<Tarefa>>

    @Headers("Content-Type: application/json")
    @POST("/tarefas")
    fun save(@Body tarefa: Tarefa, @Header("Authorization") auth: String) : Call<Tarefa>

    @DELETE("/tarefas/{id}")
    fun delete(@Path("id") id: String, @Header("Authorization") auth: String) : Call<String>

    @Headers("Content-Type: application/json")
    @POST("/tarefas/done/{id}")
    fun done(@Path("id") id: String, @Body done: Boolean, @Header("Authorization") auth: String) : Call<String>

    @Headers("Content-Type: application/json")
    @GET("/tarefas/find")
    fun find(@Query("text") text: String,
             @Header("Authorization") auth: String) : Call<List<Tarefa>>

}