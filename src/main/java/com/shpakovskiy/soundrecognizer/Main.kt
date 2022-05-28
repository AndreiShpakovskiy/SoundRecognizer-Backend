package com.shpakovskiy.soundrecognizer

import com.shpakovskiy.soundrecognizer.data.service.SoundService
import io.grpc.ServerBuilder
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

fun main() {
    runBlocking {
        ServerBuilder.forPort(8099)
            .executor(Executors.newFixedThreadPool(10))
            .addService(SoundService())
            .build().apply {
                start()

                Runtime.getRuntime().addShutdownHook(Thread {
                    shutdown()
                })

                awaitTermination()
            }
    }
}