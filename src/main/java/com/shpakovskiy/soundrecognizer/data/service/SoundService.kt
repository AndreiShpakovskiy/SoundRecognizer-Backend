package com.shpakovskiy.soundrecognizer.data.service

import com.shpakovskiy.soundrecognizer.SoundRecognizerService
import com.shpakovskiy.soundrecognizer.SoundServiceGrpc
import com.shpakovskiy.soundrecognizer.data.repository.DefaultSoundRepository
import com.shpakovskiy.soundrecognizer.recognizer.SoundSourceRecognizer
import io.grpc.stub.StreamObserver
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

class SoundService : SoundServiceGrpc.SoundServiceImplBase() {
    private val soundRecognizer = SoundSourceRecognizer()
    private val soundRepository = DefaultSoundRepository()

    init {
        val baseSoundsDirectory = File("recordings/baseRecordings")
        val directoryPath = baseSoundsDirectory.absolutePath
        val directoryFiles = baseSoundsDirectory.list().toMutableList()
        //soundRecognizer.loadBaseRecordings(directoryFiles.map { "$directoryPath/$it" })

        directoryFiles.forEach {
            soundRepository.loadSound("$directoryPath/$it") { sound ->
                //System.out.println(soundSourcePath)
                val pathParts = it.split("/")
                val noExtensionName = pathParts[pathParts.size - 1].split("\\.").toTypedArray()[0]
                val nameParts = noExtensionName.split("-").toTypedArray()
                val objectName = nameParts[nameParts.size - 2].lowercase(Locale.getDefault())

                soundRecognizer.addKnownSound(sound, objectName)
            }
        }

        println("Sounds were successfully loaded")
    }

    // FIXME: This method is implemented incorrectly at the moment
    override fun sendSound(
        request: SoundRecognizerService.Sound?,
        responseObserver: StreamObserver<SoundRecognizerService.EmptyResponse>?
    ) {

        request?.let {
            val nameParts = it.fileName.split(".") // For now assume, that we have only one dot here
            val recordingName = "recordings/baseRecordings/${nameParts[0]}-${System.currentTimeMillis()}.${nameParts[1]}"

            Files.write(
                Paths.get(recordingName),
                it.soundValues.toByteArray()
            )

            soundRepository.loadSound(Paths.get(recordingName).absolutePathString()) { loadedSound ->
                soundRecognizer.addKnownSound(loadedSound, it.fileName)
            }
        }

        println("Received file: ${request?.fileName} ${request?.soundValues?.size()} bytes")

        val response = SoundRecognizerService.EmptyResponse
            .newBuilder()
            .setReceivedSoundLength(request?.soundValues?.size() ?: -1)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    override fun recognizeSound(
        request: SoundRecognizerService.Sound?,
        responseObserver: StreamObserver<SoundRecognizerService.RecognitionResult>?
    ) {
        request?.let {
            val nameParts = it.fileName.split(".") // For now assume, that we have only one dot here
            val fileName =
                "recordings/recognitionRequests/${nameParts[0]}-${System.currentTimeMillis()}.${nameParts[1]}"
            val soundBytes = it.soundValues.toByteArray()

            Files.write(
                Paths.get(fileName),
                soundBytes
            )

            println("Recognition request: ${request.fileName} - ${soundBytes.size} bytes")

            soundRepository.loadSound(Paths.get(fileName).absolutePathString()) { sound ->
                val bestMatches = soundRecognizer.getBestMatch(sound)

                val response = SoundRecognizerService.RecognitionResult.newBuilder()

                bestMatches?.forEach { matchValue ->
                    response.addRecognitionResult(
                        SoundRecognizerService.RecognizedSound
                            .newBuilder()
                            .setSoundSourceName(matchValue.key)
                            .setProbability(matchValue.value)
                            .build()
                    )
                }

                if (bestMatches == null) {
                    println("Oops...")
                }

                responseObserver?.onNext(response.build())
                responseObserver?.onCompleted()
            }
        }
    }
}