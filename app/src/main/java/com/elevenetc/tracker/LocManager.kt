package com.elevenetc.tracker

import android.content.Context
import com.google.gson.Gson
import java.util.concurrent.LinkedBlockingQueue

class LocManager(val context: Context) {

    val writeThread = WriteThread(context)

    init {
        writeThread.start()
    }

    fun store(loc: Loc) {
        writeThread.write(loc)
    }

    fun stop() {
        writeThread.complete()
    }

    class WriteThread(val context: Context) : Thread() {

        private val queue = LinkedBlockingQueue<Loc>()
        private val gson = Gson()
        private val fileManager = FileManager()
        private val fileName = "locs"

        @Volatile
        private var running = true

        fun write(loc: Loc) {
            queue.offer(loc)
        }

        fun complete() {
            running = false
        }

        override fun run() {

            initFile()

            while (running) {
                val loc = queue.take()

                if (!running) {
                    return
                }

                val rawLocs = fileManager.readFromFile(fileName, context)
                val locs = gson.fromJson<Locs>(rawLocs, Locs::class.java)

                locs.locs.add(loc)
                fileManager.writeToFile(fileName, gson.toJson(locs), context)

                if (!running) {
                    break
                }
            }
        }


        private fun initFile() {
            val locs = fileManager.readFromFile(fileName, context)
            if (locs.isEmpty()) {
                fileManager.writeToFile(fileName, gson.toJson(Locs(mutableListOf())), context)
            }
        }

        data class Locs(val locs: MutableList<Loc>)
    }
}