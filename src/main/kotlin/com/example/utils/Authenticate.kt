package com.example.utils

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hashKey = "MySecretHashKey".toByteArray()
private val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

fun hash(pwd: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(pwd.toByteArray(Charsets.UTF_8)))
}