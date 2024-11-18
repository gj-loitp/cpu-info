package com.galaxyjoy.cpuinfo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.galaxyjoy.cpuinfo.R
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

/**
 * General utility class
 *
 * @author galaxyjoy
 */
object Utils {

    /**
     * Helper which adds item to list if value is not null and not empty
     */
    fun addPairIfExists(list: MutableList<Pair<String, String>>, key: String, value: String?) {
        if (!value.isNullOrEmpty()) {
            list.add(Pair(key, value))
        }
    }

    /**
     * Convert bytes into normalized unit string
     */
    fun humanReadableByteCount(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return "$bytes B"
        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format(
            locale = Locale.US,
            format = "%.2f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()),
            pre
        )
    }

    /**
     * Format passed bytes into megabytes string
     */
    fun convertBytesToMega(bytes: Long): String {
        val megaBytes = bytes.toDouble() / (1024.0 * 1024.0)
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

        return "${df.format(megaBytes)} MB"
    }

    /**
     * Open google with passed query
     */
    fun searchInGoogle(context: Context, query: String) {
        val uri = Uri.parse("http://www.google.com/search?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, R.string.action_not_supported, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Read and parse to Long first line from file
     */
    fun readOneLine(file: File): Double? {
        val text: String?
        try {
            val fs = FileInputStream(file)
            val sr = InputStreamReader(fs)
            val br = BufferedReader(sr)
            text = br.readLine()
            br.close()
            sr.close()
            fs.close()
        } catch (_: Exception) {
            return null
        }

        val value: Double?
        try {
            value = text.toDouble()
        } catch (_: NumberFormatException) {
            return null
        }

        return value
    }
}
