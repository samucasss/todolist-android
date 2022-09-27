package com.samuca.todolist.model

import java.io.Serializable
import java.util.*

data class Tarefa(
    val id: String?,
    val data: Date,
    val nome: String,
    val descricao: String?,
    val done: Boolean,
    var usuarioId: String?
) : Serializable
