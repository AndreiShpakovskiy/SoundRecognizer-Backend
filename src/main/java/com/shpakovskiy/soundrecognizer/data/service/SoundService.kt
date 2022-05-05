package com.shpakovskiy.soundrecognizer.data.service

import com.google.protobuf.RepeatedFieldBuilderV3
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

        baseSoundsDirectory.list()?.forEach {
            soundRepository.loadSound("$directoryPath/$it") { sound ->
                //System.out.println(soundSourcePath)
                val pathParts = it.split("/")
                val noExtensionName = pathParts[pathParts.size - 1].split("\\.").toTypedArray()[0]
                val nameParts = noExtensionName.split("-").toTypedArray()
                val objectName = nameParts[nameParts.size - 2].lowercase(Locale.getDefault())

                println(">>> Object name: $objectName")

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
            val recordingPath = Paths.get(
                "recordings/baseRecordings/${nameParts[0]}-${System.currentTimeMillis()}.${nameParts[1]}".replace(
                    ":",
                    "_"
                )
            )

            Files.write(recordingPath, it.soundValues.toByteArray())

            val pathParts = it.fileName.split("/")
            val noExtensionName = pathParts[pathParts.size - 1].split("\\.").toTypedArray()[0]
            val noExtensionNameParts = noExtensionName.split("-").toTypedArray()
            val objectName = noExtensionNameParts[noExtensionNameParts.size - 2].lowercase(Locale.getDefault())

            soundRepository.loadSound(recordingPath.absolutePathString()) { loadedSound ->
                soundRecognizer.addKnownSound(loadedSound, objectName)
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
            val filePath = Paths.get(
                "recordings/recognitionRequests/${nameParts[0]}-${System.currentTimeMillis()}.${nameParts[1]}".replace(
                    ":",
                    "_"
                )
            )
            val soundBytes = it.soundValues.toByteArray()

            Files.write(filePath, soundBytes)

            println("Recognition request: ${request.fileName} - ${soundBytes.size} bytes")

            soundRepository.loadSound(filePath.absolutePathString()) { sound ->
                val bestMatches = soundRecognizer.getBestMatch(sound)

                val response = SoundRecognizerService.RecognitionResult.newBuilder()
                response.recognitionStatus = SoundRecognizerService.RecognitionResult.RecognitionStatus.SUCCESS

                bestMatches?.forEach { matchValue ->
                    response.recognizedSounds.recognitionResultList.add(
                        SoundRecognizerService.RecognizedSound
                            .newBuilder()
                            .setSoundSourceName(matchValue.key)
                            .build()
                    )
                }

                if (bestMatches == null) {
                    response.recognitionStatus = SoundRecognizerService.RecognitionResult.RecognitionStatus.FAILURE
                }

                responseObserver?.onNext(response.build())
                responseObserver?.onCompleted()
            }
        }
    }
}