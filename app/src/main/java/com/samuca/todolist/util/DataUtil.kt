package com.samuca.todolist.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DataUtil {

    companion object {
        val FORMATO_DEFAULT = "dd/MM/yyyy"

        fun hoje(): Date {
            val calendar: Calendar = Calendar.getInstance()
            clearTimeCalendar(calendar)
            return calendar.time
        }

        fun clearTimeCalendar(calendar: Calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        fun clearTime(d: Date?): Date {
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTime(d)
            clearTimeCalendar(calendar)
            return calendar.getTime()
        }

        fun addDias(d: Date?, dias: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = d
            clearTimeCalendar(calendar)
            calendar.add(Calendar.DAY_OF_MONTH, dias)
            return calendar.time
        }

        fun format(d: Date?, formato: String?): String {
            val df = SimpleDateFormat(formato)
            return df.format(d)
        }

        fun format(d: Date?): String {
            return format(d, FORMATO_DEFAULT)
        }

        fun parse(d: String?): Date? {
            return parse(d, FORMATO_DEFAULT)
        }

        fun parse(d: String?, formato: String?): Date? {
            val sdf = SimpleDateFormat(formato)
            var retorno: Date? = null
            try {
                retorno = sdf.parse(d)
            } catch (e: ParseException) {
                System.out.println(e.message)
            }
            return retorno
        }

        fun lastSunday(d: Date): Date {
            var cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DAY_OF_WEEK, -(cal[Calendar.DAY_OF_WEEK] - 1))
            return cal.time
        }

        fun nextSunday(d: Date): Date {
            var cal = Calendar.getInstance()
            cal.time = d

            while (cal[Calendar.DAY_OF_WEEK] !== Calendar.SUNDAY) {
                cal.add(Calendar.DATE, 1)
            }

            return cal.time
        }

    }

}