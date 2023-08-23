package com.shpakovskiy.soundrecognizer

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class Sound(
    val name: String,
    val byteArray: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sound

        if (name != other.name) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}

fun main() {
    embeddedServer(Netty, port = 8099) {
        routing {
            post("/submit") {
                val multipart = call.receiveMultipart()
                val out = arrayListOf<String>()
                multipart.forEachPart { part: PartData ->
                    out += when (part) {
                        is PartData.FormItem -> {
                            "FormItem(${part.name},${part.value})"
                        }

                        is PartData.FileItem -> {
                            val bytes = part.streamProvider().readBytes()
                            "FileItem(${part.name}, ${part.originalFileName}, ${bytes.size})"
                        }

                        is PartData.BinaryItem -> {
                            "BinaryItem(${part.name})"
                        }

                        else -> "Unknown"
                    }

                    part.dispose()
                }

                println("Submit: $out")

                call.respond(HttpStatusCode.Accepted)
            }
            post("/recognize") {
                val multipart = call.receiveMultipart()
                val out = arrayListOf<String>()
                multipart.forEachPart { part: PartData ->
                    out += when (part) {
                        is PartData.FormItem -> {
                            "FormItem(${part.name},${part.value})"
                        }

                        is PartData.FileItem -> {
                            val bytes = part.streamProvider().readBytes()
                            "FileItem(${part.name}, ${part.originalFileName}, ${bytes.size})"
                        }

                        is PartData.BinaryItem -> {
                            "BinaryItem(${part.name})"
                        }

                        else -> "Unknown"
                    }

                    part.dispose()
                }

                println("Recognize: $out")

                call.respond("Recognized Object")
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