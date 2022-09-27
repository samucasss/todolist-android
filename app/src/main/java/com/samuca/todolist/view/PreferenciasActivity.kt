package com.samuca.todolist.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.samuca.todolist.MainActivity
import com.samuca.todolist.R
import com.samuca.todolist.auth.Auth
import com.samuca.todolist.databinding.ActivityPreferenciasBinding
import com.samuca.todolist.model.Preferencias
import com.samuca.todolist.model.TipoFiltro
import com.samuca.todolist.rest.NetworkUtils
import com.samuca.todolist.rest.PreferenciasRest
import kotlinx.android.synthetic.main.activity_preferencias.*
import kotlinx.android.synthetic.main.activity_preferencias.delete
import kotlinx.android.synthetic.main.activity_preferencias.ok
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PreferenciasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferenciasBinding
    private var tipoFiltroSelected: TipoFiltro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreferenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle(R.string.preferencias_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            navigateUpTo(Intent(baseContext, MainActivity::class.java))
        }

        val adapter = ArrayAdapter(this, R.layout.list_item, TipoFiltro.values())
        tipoFiltro.setAdapter(adapter)
        tipoFiltro.setOnItemClickListener { adapterView, view, i, l ->
            tipoFiltroSelected = adapterView.getItemAtPosition(i) as TipoFiltro
        }

        getPreferencias()

        ok.setOnClickListener {
            save()
        }

        delete.setOnClickListener {
            confirmDelete()
        }
    }

    private fun getPreferencias() {
        val auth: Auth = Auth.getInstance(baseContext)
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(PreferenciasRest::class.java)
        val callback = endpoint.get("Bearer " + auth.authToken)

        callback.enqueue(object : Callback<Preferencias> {
            override fun onFailure(call: Call<Preferencias>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Preferencias>, response: Response<Preferencias>) {
                val preferencias: Preferencias? = response.body()
                println("preferencias: " + preferencias)

                if (preferencias != null) {
                    val tipo: TipoFiltro? = TipoFiltro.findById(preferencias.tipoFiltro)
                    tipoFiltro.setText(tipo?.nome, false)

                    tipoFiltroSelected = tipo
                    done.isChecked = preferencias.done
                }
            }
        })
    }

    private fun save() {
        if (tipoFiltroSelected == null) {
            tipoFiltro.error = "Favor informar o Tipo filtro"
            tipoFiltro.requestFocus()
            return
        }

        val preferencias = Preferencias(null, tipoFiltroSelected!!.id, done.isChecked, null)

        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(PreferenciasRest::class.java)
        val callback = endpoint.save(preferencias, "Bearer " + auth.authToken)

        callback.enqueue(object : Callback<Preferencias> {
            override fun onFailure(call: Call<Preferencias>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Preferencias>, response: Response<Preferencias>) {
                val preferencias: Preferencias? = response.body()
                println("preferencias: " + preferencias)

                if (preferencias != null) {
                    Toast.makeText(baseContext, "Operação realizada com sucesso", Toast.LENGTH_SHORT).show()
                    navigateUpTo(Intent(baseContext, MainActivity::class.java))
                }
            }
        })
    }

    private fun confirmDelete() {
        val builder = AlertDialog.Builder(this)

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            delete()
        }

        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        }

        with(builder)
        {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir o registro de preferências?")
            setPositiveButton(R.string.button_yes, DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(R.string.button_no, DialogInterface.OnClickListener(function = negativeButtonClick))
            show()
        }
    }

    private fun delete() {
        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(PreferenciasRest::class.java)
        val callback = endpoint.delete("Bearer " + auth.authToken)

        callback.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val ok: String? = response.body()
                println("ok: " + ok)

                if (response.code() == 200) {
                    Toast.makeText(baseContext, "Operação realizada com sucesso", Toast.LENGTH_SHORT).show()
                    navigateUpTo(Intent(baseContext, MainActivity::class.java))
                }
            }
        })
    }

}