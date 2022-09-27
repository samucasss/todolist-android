package com.samuca.todolist.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.samuca.todolist.MainActivity
import com.samuca.todolist.R
import com.samuca.todolist.auth.Auth
import com.samuca.todolist.databinding.ActivityUsuarioBinding
import com.samuca.todolist.model.Login
import com.samuca.todolist.model.Usuario
import com.samuca.todolist.rest.AuthRest
import com.samuca.todolist.rest.NetworkUtils
import com.samuca.todolist.rest.UsuarioRest
import kotlinx.android.synthetic.main.activity_usuario.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsuarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            navigateUpTo(Intent(baseContext, MainActivity::class.java))
        }

        val user: Usuario? = Auth.getInstance(this).user()
        if (user != null) {
            binding.toolbar.setTitle(R.string.usuario_title_meusdados)
            delete.isVisible = true

            nome.setText(user.nome)
            email.setText(user.email)

            delete.setOnClickListener {
                confirmDelete()
            }

        } else {
            binding.toolbar.setTitle(R.string.usuario_title_registro)
            delete.isVisible = false
        }

        ok.setOnClickListener {
            if (validateFields()) {
                if (user != null) {
                    save()

                } else {
                    register()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        if (TextUtils.isEmpty(nome?.text.toString())) {
            nome.error = "Favor informar o nome"
            nome.requestFocus()

            return false
        }

        if (TextUtils.isEmpty(email?.text.toString())) {
            email.error = "Favor informar o email"
            email.requestFocus()

            return false
        }

        if (TextUtils.isEmpty(senha?.text.toString())) {
            senha.error = "Favor informar a senha"
            senha.requestFocus()

            return false
        }

        return true
    }

    private fun register() {
        val usuario = Usuario(null, nome.text.toString(), email.text.toString(), senha.text.toString())

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(AuthRest::class.java)
        val callback = endpoint.register(usuario)

        callback.enqueue(object : Callback<Usuario> {
            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                val usuario: Usuario? = response.body()
                println("usuario: " + usuario)

                if (usuario != null) {
                    Toast.makeText(baseContext, "Usuário registrado com sucesso", Toast.LENGTH_SHORT).show()
                    login()
                }
            }
        })
    }

    private fun save() {
        val usuario = Usuario(null, nome.text.toString(), email.text.toString(), senha.text.toString())

        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(UsuarioRest::class.java)
        val callback = endpoint.save(usuario, "Bearer " + auth.authToken)

        callback.enqueue(object : Callback<Usuario> {
            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                val usuario: Usuario? = response.body()
                println("usuario: " + usuario)

                if (usuario != null) {
                    auth.updateUser(usuario.nome, usuario.email)
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
            setMessage("Tem certeza que deseja excluir o usuário?")
            setPositiveButton(R.string.button_yes, DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(R.string.button_no, DialogInterface.OnClickListener(function = negativeButtonClick))
            show()
        }
    }

    private fun delete() {
        val auth: Auth = Auth.getInstance(baseContext)

        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(UsuarioRest::class.java)
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
                    auth.deleteUser()
                    Toast.makeText(baseContext, "Operação realizada com sucesso", Toast.LENGTH_SHORT).show()
                    navigateUpTo(Intent(baseContext, MainActivity::class.java))
                }
            }
        })
    }

    private fun login() {
        val login = Login(email.text.toString(), senha.text.toString())
        Auth.getInstance(baseContext).login(this, login)
    }


}