package com.galaxyjoy.cpuinfo.feat.processes

import androidx.annotation.VisibleForTesting
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Read and parse processes form ps
 *
 * @author galaxyjoy
 */
@Singleton
class PsProvider @Inject constructor() {

    companion object {
        private const val USER_POSITION = 0
        private const val PID_POSITION = 1
        private const val PPID_POSITION = 2
        private const val VSIZE_POSITION = 3
        private const val RSS_POSITION = 4
        private const val NICENESS_POSITION = 6
        private const val NAME_POSITION = 12
    }

    /**
     * Return [Single] with list of [ProcessItem]. It will throw onError in case of internal
     * exception
     */
    fun getPsList(): Single<List<ProcessItem>> {
        return Single.fromCallable {
            val psCmdList = readPsCmd()
            return@fromCallable parsePs(psCmdList)
        }
    }

    /**
     * Get output from ps command as a [List] of Strings
     */
    private fun readPsCmd(): List<String> {
        val processList = ArrayList<String>()
        try {
            val args = arrayListOf("/system/bin/ps", "-p")
            val cmd = ProcessBuilder(args)
            val process = cmd.start()
            val bis = process.inputStream.bufferedReader()
            processList.addAll(bis.readLines())
        } catch (e: Exception) {
            Timber.e(e)
        }
        return processList
    }

    /**
     * Parse output from ps command and return [List] of [ProcessItem]
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun parsePs(processLines: List<String>): List<ProcessItem> {
        val processItemList = ArrayList<ProcessItem>()
        processLines.forEachIndexed { i, line ->
            if (i > 0) {
                val st = StringTokenizer(line)
                var iterator = 0
                var user = ""
                var name = ""
                var pid = ""
                var ppid = ""
                var niceness = ""
                var rss = ""
                var vsize = ""

                while (st.hasMoreTokens()) {
                    when (iterator) {
                        USER_POSITION -> user = st.nextToken()
                        PID_POSITION -> pid = st.nextToken()
                        PPID_POSITION -> ppid = st.nextToken()
                        VSIZE_POSITION -> vsize = st.nextToken()
                        RSS_POSITION -> rss = st.nextToken()
                        NICENESS_POSITION -> niceness = st.nextToken()
                        NAME_POSITION -> name = st.nextToken()
                        else -> st.nextToken()
                    }
                    iterator++
                }

                processItemList.add(ProcessItem(name, pid, ppid, niceness, user, rss, vsize))
            }
        }
        return processItemList
    }
}
