package com.example.project1.qrscanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel to manage QR code scanning state and results
 */
class QRCodeViewModel : ViewModel() {

    // Scanning state
    private val _scanningState = MutableStateFlow<ScanningState>(ScanningState.NotScanning)
    val scanningState: StateFlow<ScanningState> = _scanningState.asStateFlow()

    // Last scanned result
    private val _lastScannedResult = MutableStateFlow<String?>(null)
    val lastScannedResult: StateFlow<String?> = _lastScannedResult.asStateFlow()

    /**
     * Start scanning process
     */
    fun startScanning() {
        _scanningState.value = ScanningState.Scanning
    }

    /**
     * Handle detected QR code
     */
    fun onQRCodeDetected(result: String) {
        _lastScannedResult.value = result

        // Validate if the result is a URL
        if (result.startsWith("http://") || result.startsWith("https://")) {
            _scanningState.value = ScanningState.SuccessfulScan(result)
        } else {
            _scanningState.value = ScanningState.InvalidResult(result)
        }
    }

    /**
     * Reset scanner state
     */
    fun resetScanner() {
        _scanningState.value = ScanningState.NotScanning
    }

    /**
     * Clear last scanned result
     */
    fun clearLastResult() {
        _lastScannedResult.value = null
    }
}

/**
 * Represents the different states of QR code scanning
 */
sealed class ScanningState {
    object NotScanning : ScanningState()
    object Scanning : ScanningState()
    data class SuccessfulScan(val result: String) : ScanningState()
    data class InvalidResult(val result: String) : ScanningState()
    data class Error(val message: String) : ScanningState()
}