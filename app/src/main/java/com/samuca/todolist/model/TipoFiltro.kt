package com.samuca.todolist.model

import com.samuca.todolist.util.DataUtil
import com.samuca.todolist.util.Periodo
import java.util.*

enum class TipoFiltro(val id: String, val nome: String) {
    TODOS("T", "Todos") {
        override fun getPeriodo(d: Date): Periodo {
            val inicio: Date = DataUtil.addDias(d, -10)
            val fim: Date = DataUtil.addDias(d, 10)
            return Periodo(inicio, fim)
        }
    },
    HOJE("H", "Hoje") {
        override fun getPeriodo(d: Date): Periodo {
            val inicio: Date = d
            val fim: Date = DataUtil.addDias(d, 1)
            return Periodo(inicio, fim)
        }
    },
    AMANHA("A", "Amanhã") {
        override fun getPeriodo(d: Date): Periodo {
            val inicio: Date = DataUtil.addDias(d, 1)
            val fim: Date = DataUtil.addDias(d, 2)
            return Periodo(inicio, fim)
        }
    },
    SEMANA("S", "Semana") {
        override fun getPeriodo(d: Date): Periodo {
            val inicio: Date = DataUtil.lastSunday(d)
            val fim: Date = DataUtil.nextSunday(d)
            return Periodo(inicio, fim)
        }
    };

    companion object {
        fun findById(id: String): TipoFiltro? {
            for (tipoFiltro in values()) {
                if (tipoFiltro.id.equals(id)) {
                    return tipoFiltro
                }
            }

            return null
        }
    }

    abstract fun getPeriodo(d: Date): Periodo

    fun getPeriodo(): Periodo {
        val hoje = DataUtil.hoje()
        return getPeriodo(hoje)
    }
}