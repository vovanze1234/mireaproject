package com.example.proga

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Random
import java.io.Console

class MyWorker (context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val result = performBackgroundTask()

        return if (result) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun performBackgroundTask(): Boolean {
        for (i in 1..10) {
            val random = Random()
            val x = random.nextInt(100)
            println(x)
            Thread.sleep(1000)
        }
        return true
    }
}