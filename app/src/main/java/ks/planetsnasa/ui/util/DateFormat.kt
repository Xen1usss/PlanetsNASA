package ks.planetsnasa.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val prettyFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofPattern("d MMM uuuu")
}

fun String.formatIsoToLocalOrSelf(): String = try {
    val zdt = Instant.parse(this).atZone(ZoneId.systemDefault())
    prettyFormatter.format(zdt)
} catch (_: Exception) {
    this
}