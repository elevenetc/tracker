package com.elevenetc.tracker

import android.content.Context
import android.os.Handler
import com.google.gson.Gson
import java.util.concurrent.LinkedBlockingQueue

class LocManager(val context: Context) {

    private val fileName = "locs"
    val writeThread = WriteThread(fileName, context)
    val readThread = ReadThread(fileName, context)

    init {
        writeThread.start()
        readThread.start()
    }

    fun store(loc: Loc) {
        writeThread.write(loc)
    }

    fun getAll(h: Handler) {
        readThread.getLocs(object : ReadThread.ReadData {
            override fun onReady(locs: List<Loc>) {
                val message = h.obtainMessage()
                message.obj = locs
                message.sendToTarget()
            }
        })
    }

    fun stop() {
        writeThread.complete()
    }

    class ReadThread(val fileName: String, val context: Context) : Thread() {

        private val queue = LinkedBlockingQueue<ReadData>()

        private val gson = Gson()
        private val fileManager = FileManager()

        @Volatile
        private var running = true

        fun getLocs(handler: ReadData) {
            queue.offer(handler)
        }

        fun complete() {
            running = false
        }

        override fun run() {
            while (running) {
                val consumer = queue.take()

                try {
                    val rawLocs = fileManager.readFromFile(fileName, context)
                    val locs = gson.fromJson<WriteThread.Locs>(rawLocs, WriteThread.Locs::class.java)
                    consumer.onReady(locs.locs)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    consumer.onReady(emptyList())
                }
            }
        }

        interface ReadData {
            fun onReady(locs: List<Loc>)
        }
    }

    class WriteThread(val fileName: String, val context: Context) : Thread() {

        private val queue = LinkedBlockingQueue<Loc>()
        private val gson = Gson()
        private val fileManager = FileManager()

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