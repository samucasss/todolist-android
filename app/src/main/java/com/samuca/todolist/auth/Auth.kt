package com.samuca.todolist.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.samuca.todolist.MainActivity
import com.samuca.todolist.model.Login
import com.samuca.todolist.model.Token
import com.samuca.todolist.model.Usuario
import com.samuca.todolist.rest.AuthRest
import com.samuca.todolist.rest.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Auth private constructor(context: Context) {

    private val AUTH_TOKEN: String = "AUTH_TOKEN"
    private val AUTH_USER: String = "AUTH_USER"
    private val preferences: SharedPreferences = context.getSharedPreferences(AUTH_TOKEN, Context.MODE_PRIVATE)

    var authToken: String?
        get() = preferences.getString(AUTH_TOKEN, "")
        private set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    private var user: Usuario?
        get() {
            val userString: String? = preferences.getString(AUTH_USER, "")
            if (!userString.isNullOrBlank()) {
                var gson = Gson()
                return gson.fromJson(userString, Usuario::class.java)
            }

            return null
        }
        private set(value) {
            if (value != null) {
                var gson = Gson()
                preferences.edit().putString(AUTH_USER, gson.toJson(value)).apply()

            } else {
                preferences.edit().putString(AUTH_USER, "").apply()
            }
        }

    companion object {
        private var instance: Auth? = null

        fun getInstance(context: Context) : Auth {
            if(instance == null) {
                instance = Auth(context)
            }

            return instance!!
        }
    }

    fun isLogged(): Boolean {
        println("user: " + user)
        return user != null
    }

    fun user(): Usuario? {
        return user
    }

    fun logout() {
        authToken = ""
        user = null
    }

    fun updateUser(nome: String, email: String) {
        var usuario = Usuario(user?.id, nome, email, null)
        user = usuario
    }

    fun deleteUser() {
        authToken = ""
        user = null
    }

    fun login(activity: Activity, login: Login) {
        val context: Context = activity.baseContext
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(AuthRest::class.java)
        val callback = endpoint.login(login)

        val auth: Auth = getInstance(context)

        callback.enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>, t: Throwable) {
                println(t.message)
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.code() == 200) {
                    val token: Token? = response.body()
                    println("token: " + token)

                    if (token != null) {
                        auth.authToken = token.token
                        getUser(activity)
                    }

                } else {
                    Toast.makeText(context, "Login inválido", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    fun getUser(activity: Activity) {
        val context: Context = activity.baseContext
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(AuthRest::class.java)
        val callback = endpoint.get("Bearer " + authToken)

        val auth: Auth = getInstance(context)

        callback.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                println(t.message)
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                println("response.code(): " + response.code())

                if (response.code() == 200) {
                    val user: User? = response.body()
                    println("user: " + user)

                    if (user != null) {
                        auth.user = user.user
                        activity.navigateUpTo(Intent(context, MainActivity::class.java))
                    }

                } else {
                    Toast.makeText(context, "Erro ao recuperar usuário", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}