package com.palpay.ussdlauncher.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class CapturedUssdData(
    val name: String,
    val phone: String,
    val fullText: String
)

object UssdCaptureBus {
    private val _events = MutableSharedFlow<CapturedUssdData>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emit(data: CapturedUssdData) {
        _events.tryEmit(data)
    }
}
