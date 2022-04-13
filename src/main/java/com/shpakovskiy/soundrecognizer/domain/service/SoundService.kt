package com.shpakovskiy.soundrecognizer.domain.service

import com.shpakovskiy.soundrecognizer.SoundRecognizerService
import com.shpakovskiy.soundrecognizer.SoundServiceGrpc
import io.grpc.stub.StreamObserver
import java.nio.file.Files
import java.nio.file.Paths

class SoundService : SoundServiceGrpc.SoundServiceImplBase() {
    override fun sendSound(
        request: SoundRecognizerService.Sound?,
        responseObserver: StreamObserver<SoundRecognizerService.EmptyResponse>?
    ) {
        request?.let {
            Files.write(
                Paths.get("recordings/recognitionRequests/${it.fileName}"),
                it.soundValues.toByteArray()
            )
        }

        println("Received file: ${request?.soundValues?.size()} bytes")

        val response = SoundRecognizerService.EmptyResponse
            .newBuilder()
            .setReceivedSoundLength(request?.soundValues?.size() ?: -1)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
        super.sendSound(request, responseObserver)
    }
}