package com.samuca.todolist.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.datepicker.MaterialDatePicker
import com.samuca.todolist.MainActivity
import com.samuca.todolist.R
import com.samuca.todolist.auth.Auth
import com.samuca.todolist.databinding.ActivityTarefaFormBinding
import com.samuca.todolist.model.Tarefa
import com.samuca.todolist.rest.NetworkUtils
import com.samuca.todolist.rest.TarefaRest
import com.samuca.todolist.util.DataUtil
import kotlinx.android.synthetic.main.activity_tarefa_form.*
import kotlinx.android.synthetic.main.activity_tarefa_form.delete
import kotlinx.android.synthetic.main.activity_tarefa_form.nome
import kotlinx.android.synthetic.main.activity_tarefa_form.ok
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TarefaFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTarefaFormBinding

    private var dataSelecionada: Date = Date()
    private var tarefaEdit: Tarefa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarefaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            navigateUpTo(Intent(baseContext, MainActivity::class.java))
        }

        //verifica se é edição de tarefa
        if (intent.hasExtra("tarefa")) {
            binding.toolbar.setTitle(R.string.tarefa_editar_title)

            val tarefa: Tarefa = intent.getSerializableExtra("tarefa") as Tarefa
            tarefaEdit = tarefa

            dataSelecionada = tarefa.data
            nome.setText(tarefa.nome)
            descricao.setText(tarefa.descricao)
            done.isChecked = tarefa.done

            delete.isVisible = true
            delete.setOnClickListener {
                confirmDelete()
            }

        } else {
            binding.toolbar.setTitle(R.string.tarefa_novo_title)
            delete.isVisible = false
        }

        val formatter = SimpleDateFormat("E, dd/MM/yyyy", Locale("pt", "BR"))
        data.setText(formatter.format(dataSelecionada))

        data.setOnClickListener {
            Locale.setDefault(Locale("pt", "BR"))

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(supportFragmentManager, "Data")

            datePicker.addOnPositiveButtonClickListener {
                dataSelecionada = Date(it)
                data.setText(formatter.format(dataSelecionada))
            }
        }

        ok.setOnClickListener {
            if (validateFields()) {
                save()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (TextUtils.isEmpty(nome?.text.toString())) {
            nome.error = "Favor informar o nome"
            nome.requestFocus()

            return false
        }

        return true
    }

    private fun save() {
        var id: String? = if (tarefaEdit != null) tarefaEdit?.id else null
        val dataSemTime = DataUtil.clearTime(dataSelecionada)
        val tarefa = Tarefa(id, dataSemTime, nome.text.toString(), descricao.text.toString(),
            done.isChecked, null)

        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(TarefaRest::class.java)
        val callback = endpoint.save(tarefa, "Bearer " + auth.authToken)

        callback.enqueue(object : Callback<Tarefa> {
            override fun onFailure(call: Call<Tarefa>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Tarefa>, response: Response<Tarefa>) {
                val tarefa: Tarefa? = response.body()
                println("tarefa: " + tarefa)

                if (tarefa != null) {
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
            setMessage("Tem certeza que deseja excluir a tarefa?")
            setPositiveButton(R.string.button_yes, DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(R.string.button_no, DialogInterface.OnClickListener(function = negativeButtonClick))
            show()
        }
    }

    private fun delete() {
        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(TarefaRest::class.java)
        val callback = endpoint.delete(tarefaEdit?.id!!, "Bearer " + auth.authToken)

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