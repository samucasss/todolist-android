package com.samuca.todolist.rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {

    companion object {

        private val URL_API: String = "http://10.0.2.2:3001"

        /** Retorna uma Instância do Client Retrofit para Requisições
         * @param path Caminho Principal da API
         */
        fun getRetrofitInstance() : Retrofit {
            return Retrofit.Builder()
                .baseUrl(URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}