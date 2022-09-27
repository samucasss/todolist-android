package com.samuca.todolist.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.samuca.todolist.R
import com.samuca.todolist.model.Tecnologia
import com.samuca.todolist.rest.NetworkUtils
import com.samuca.todolist.rest.TecnologiaRest
import com.samuca.todolist.view.adapter.TecnologiaListAdapter
import kotlinx.android.synthetic.main.activity_tecnologia.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TecnologiaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.tecnologia_title)
        setContentView(R.layout.activity_tecnologia)

        listViewTecnologias.setLayoutManager(LinearLayoutManager(this))
        listViewTecnologias.adapter = TecnologiaListAdapter(arrayListOf(),this)
        getData()
    }

    fun getData() {
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(TecnologiaRest::class.java)
        val callback = endpoint.findAll()

        callback.enqueue(object : Callback<List<Tecnologia>> {
            override fun onFailure(call: Call<List<Tecnologia>>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Tecnologia>>, response: Response<List<Tecnologia>>) {
                val tecnologiaList: List<Tecnologia>? = response.body()

                if (tecnologiaList != null) {
                    val adapter: TecnologiaListAdapter = listViewTecnologias.adapter as TecnologiaListAdapter
                    adapter.addAll(tecnologiaList)
                }
            }
        })

    }
}