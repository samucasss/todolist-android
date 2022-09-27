package com.samuca.todolist

import com.samuca.todolist.model.TipoFiltro
import com.samuca.todolist.util.DataUtil
import com.samuca.todolist.util.Periodo
import org.junit.Test
import java.util.*
import org.junit.Assert.*

class TipoFiltroTest {

    @Test
    fun testPeriodoFiltroTodos() {
        val referencia: Date? = DataUtil.parse("15/09/2022")
        val inicio: Date? = DataUtil.parse("05/09/2022")
        val fim: Date? = DataUtil.parse("25/09/2022")
        val periodoExpected = Periodo(inicio!!, fim!!)

        val periodoFiltro = TipoFiltro.TODOS.getPeriodo(referencia!!)
        assertEquals(periodoExpected, periodoFiltro)
    }

    @Test
    fun testPeriodoFiltroHoje() {
        val referencia: Date? = DataUtil.parse("15/09/2022")
        val inicio: Date? = DataUtil.parse("15/09/2022")
        val fim: Date? = DataUtil.parse("16/09/2022")
        val periodoExpected = Periodo(inicio!!, fim!!)

        val periodoFiltro = TipoFiltro.HOJE.getPeriodo(referencia!!)
        assertEquals(periodoExpected, periodoFiltro)
    }

    @Test
    fun testPeriodoFiltroAmanha() {
        val referencia: Date? = DataUtil.parse("15/09/2022")
        val inicio: Date? = DataUtil.parse("16/09/2022")
        val fim: Date? = DataUtil.parse("17/09/2022")
        val periodoExpected = Periodo(inicio!!, fim!!)

        val periodoFiltro = TipoFiltro.AMANHA.getPeriodo(referencia!!)
        assertEquals(periodoExpected, periodoFiltro)
    }

    @Test
    fun testPeriodoFiltroSemana() {
        val referencia: Date? = DataUtil.parse("26/09/2022")
        val inicio: Date? = DataUtil.parse("25/09/2022")
        val fim: Date? = DataUtil.parse("02/10/2022")
        val periodoExpected = Periodo(inicio!!, fim!!)

        val periodoFiltro = TipoFiltro.SEMANA.getPeriodo(referencia!!)
        assertEquals(periodoExpected, periodoFiltro)
    }

}