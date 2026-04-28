package com.palpay.ussdlauncher.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class UssdAccessibilityService : AccessibilityService() {

    companion object {
        private val DIALER_PACKAGES = setOf(
            "com.android.phone",
            "com.android.dialer",
            "com.google.android.dialer",
            "com.samsung.android.dialer",
            "com.huawei.phone",
            "com.coloros.phone",
            "com.oppo.phone",
            "com.miui.calls"
        )

        private val NAME_PATTERNS = listOf(
            Regex("المستفيد[:\\s]+([\\u0600-\\u06FF\\s]+)"),
            Regex("اسم[:\\s]+([\\u0600-\\u06FF\\s]+)"),
            Regex("المُستلم[:\\s]+([\\u0600-\\u06FF\\s]+)"),
            Regex("المستلم[:\\s]+([\\u0600-\\u06FF\\s]+)"),
            Regex("Beneficiary[:\\s]+([A-Za-z\\s]+)"),
            Regex("Name[:\\s]+([A-Za-z\\s]+)"),
            Regex("Recipient[:\\s]+([A-Za-z\\s]+)"),
            Regex("Account Name[:\\s]+([A-Za-z\\s]+)"),
            Regex("Account Holder[:\\s]+([A-Za-z\\s]+)")
        )

        private val PHONE_PATTERN = Regex("(05[0-9]{8}|\\+9725[0-9]{8}|9725[0-9]{8})")
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
            packageNames = DIALER_PACKAGES.toTypedArray()
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val eventPackage = event.packageName?.toString() ?: return
        if (!DIALER_PACKAGES.contains(eventPackage)) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            val source = event.source ?: return
            val fullText = extractAllText(source)
            source.recycle()

            if (fullText.isBlank()) return

            val capturedName = extractName(fullText)
            val capturedPhone = extractPhone(fullText)

            if (capturedName != null || capturedPhone != null) {
                UssdCaptureBus.emit(
                    CapturedUssdData(
                        name = capturedName ?: "",
                        phone = capturedPhone ?: "",
                        fullText = fullText
                    )
                )
            }
        }
    }

    private fun extractAllText(node: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        collectText(node, sb, depth = 0)
        return sb.toString().trim()
    }

    private fun collectText(node: AccessibilityNodeInfo, sb: StringBuilder, depth: Int) {
        if (depth > 10) return
        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()
        if (!text.isNullOrBlank()) sb.append(text).append("\n")
        if (!desc.isNullOrBlank() && desc != text) sb.append(desc).append("\n")
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectText(child, sb, depth + 1)
            child.recycle()
        }
    }

    private fun extractName(text: String): String? {
        for (pattern in NAME_PATTERNS) {
            val match = pattern.find(text)
            if (match != null) {
                val name = match.groupValues[1].trim()
                if (name.isNotBlank() && name.length > 1) return name
            }
        }
        return null
    }

    private fun extractPhone(text: String): String? {
        return PHONE_PATTERN.find(text)?.value
    }

    override fun onInterrupt() {
    }
}
