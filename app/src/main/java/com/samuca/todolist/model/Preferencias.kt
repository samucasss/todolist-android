package com.samuca.todolist.model

data class Preferencias(
    var id: String?,
    val tipoFiltro: String,
    val done: Boolean,
    var usuarioId: String?
)
