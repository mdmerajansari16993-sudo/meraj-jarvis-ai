package com.example.security

import java.util.Locale

object SecurityShield {

    const val ILLEGAL_RESPONSE: String =
        "Mujhe maaf karna, main is kaam mein aapki madad nahi kar sakta. Yeh illegal hai."

    private val ILLEGAL_PATTERNS = listOf(
        "hack",
        "hacking",
        "hacker",
        "crack",
        "cracking",
        "cracked",
        "bypass",
        "bypassing",
        "exploit",
        "ddos",
        "botnet",
        "keylogger",
        "trojan",
        "malware",
        "ransomware",
        "spyware",
        "carding",
        "steal otp",
        "otp bypass",
        "dump cvv",
        "phishing",
        "aimbot",
        "wallhack",
        "mod apk hack",
        "unauthorized bypass",
        "brute force",
        "wifi password hack",
        "crack password",
        "steal data",
        "identity theft"
    )

    private val DUMMY_PAN_PATTERNS = setOf(
        "AAAAA0000A",
        "BBBBB0000B",
        "CCCCC0000C",
        "ABCDE1234A",
        "ABCDE1234F",
        "TESTP1234T",
        "XXXXX0000X",
        "PANNO1234A"
    )

    fun evaluatePrompt(prompt: String): SecurityCheckResult {
        val normalized = prompt.lowercase(Locale.ROOT)
        for (pattern in ILLEGAL_PATTERNS) {
            if (normalized.contains(pattern)) {
                return SecurityCheckResult.Blocked(
                    detectedKeyword = pattern,
                    message = ILLEGAL_RESPONSE
                )
            }
        }
        return SecurityCheckResult.Safe
    }

    sealed class SecurityCheckResult {
        object Safe : SecurityCheckResult()
        data class Blocked(val detectedKeyword: String, val message: String) : SecurityCheckResult()
    }

    data class PanValidationResult(
        val isValid: Boolean,
        val message: String
    )

    fun validatePanCard(panInput: String): PanValidationResult {
        val trimmed = panInput.trim().uppercase(Locale.ROOT)
        if (trimmed.isEmpty()) {
            return PanValidationResult(false, "PAN card number cannot be empty.")
        }
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")
        if (!panRegex.matches(trimmed)) {
            return PanValidationResult(
                false,
                "Invalid PAN format! PAN must be 10 characters: 5 letters, 4 numbers, 1 letter (e.g. ABCDE1234F)."
            )
        }
        if (DUMMY_PAN_PATTERNS.contains(trimmed)) {
            return PanValidationResult(
                false,
                "Dummy/Sample PAN rejected. E-commerce seller integration strictly requires real Government-issued credentials."
            )
        }
        val fourthChar = trimmed[3]
        if (fourthChar !in listOf('P', 'C', 'H', 'F', 'A', 'T', 'B', 'L', 'J', 'G')) {
            return PanValidationResult(
                false,
                "Invalid PAN card entity category code (4th letter '$fourthChar')."
            )
        }
        return PanValidationResult(true, "PAN card verified successfully for merchant onboarding.")
    }
}
