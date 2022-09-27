package com.samuca.todolist.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.samuca.todolist.MainActivity
import com.samuca.todolist.R
import com.samuca.todolist.auth.Auth
import com.samuca.todolist.databinding.ActivityLoginBinding
import com.samuca.todolist.model.Login
import kotlinx.android.synthetic.main.activity_usuario.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle(R.string.login_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            navigateUpTo(Intent(baseContext, MainActivity::class.java))
        }

        ok.setOnClickListener {
            if (validateFields()) {
                login()
            }
        }
    }

    private fun validateFields(): Boolean {
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

    private fun login() {
        val login = Login(email.text.toString(), senha.text.toString())
        Auth.getInstance(baseContext).login(this, login)
    }

}