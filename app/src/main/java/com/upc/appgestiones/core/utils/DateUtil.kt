package com.upc.appgestiones.core.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.Date

class DateUtil {

    companion object {

        fun diferenciaDeFechas(fecIni:String, fecFin:String) : Long {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val fechaInicio = LocalDate.parse(fecIni, formatter)
            val fechaFin = LocalDate.parse(fecFin, formatter)

            return ChronoUnit.DAYS.between(fechaInicio, fechaFin)
        }

        fun diferenciaDeFechaActual(fec: String): Long {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val fechaActual = LocalDate.now()
            val fechaFin = LocalDate.parse(fec, formatter)

            return ChronoUnit.DAYS.between(fechaActual, fechaFin)
        }
    }
}