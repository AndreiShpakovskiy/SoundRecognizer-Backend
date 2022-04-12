package com.shpakovskiy.soundrecognizer.domain.service

import com.shpakovskiy.soundrecognizer.SoundRecognizerService
import com.shpakovskiy.soundrecognizer.SoundServiceGrpc
import io.grpc.stub.StreamObserver

class SoundService : SoundServiceGrpc.SoundServiceImplBase() {
    override fun sendSound(
        request: SoundRecognizerService.Sound?,
        responseObserver: StreamObserver<SoundRecognizerService.EmptyResponse>?
    ) {
        println("Received file: ${request?.soundValues?.size()} bytes")
        super.sendSound(request, responseObserver)
    }
}