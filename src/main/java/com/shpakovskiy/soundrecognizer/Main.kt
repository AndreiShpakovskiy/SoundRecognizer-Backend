package com.shpakovskiy.soundrecognizer

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8099) {
        routing {
            get("/") {
                call.respond("Want to home")
            }
        }
    }.start(wait = true)

    /*
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
     */
}