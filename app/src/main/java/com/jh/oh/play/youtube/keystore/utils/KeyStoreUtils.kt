package com.jh.oh.play.youtube.keystore.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import timber.log.Timber
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

object KeyStoreUtils {
    private const val ALIAS = "popCaster_key"
    private const val KEYSTORE = "AndroidKeyStore"
    private const val ALGORITHM: String =
        "${KeyProperties.KEY_ALGORITHM_RSA}/${KeyProperties.BLOCK_MODE_ECB}/${KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1}"

    private var keyStore: KeyStore = KeyStore.getInstance(KEYSTORE).apply {
        load(null)
    }

    init {
        if(keyStore.containsAlias(ALIAS).not()) {
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE).apply {
                initialize(
                    KeyGenParameterSpec.Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1).build()
                )
                generateKeyPair()
            }
        }
    }

    fun encrypt(plainText: String?): String = plainText?.run {
        Cipher.getInstance(ALGORITHM).run {
            val privateKeyEntry = keyStore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
            init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
            val byteText = toByteArray(Charsets.UTF_8)
            val encryptedByteText = doFinal(byteText)
            val encodeByteText = Base64.encode(encryptedByteText, Base64.DEFAULT)
            Timber.d("KeyStore Test KeyStoreUtils encrypt plainText : $plainText, encryptText : ${encodeByteText.toString(Charsets.UTF_8)}")
            encodeByteText.toString(Charsets.UTF_8)
        }
    }?: ""

    fun decrypt(encodeText: String?): String = encodeText?.run {
        Cipher.getInstance(ALGORITHM).run {
            val privateKeyEntry = keyStore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
            init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
            val byteText = toByteArray(Charsets.UTF_8)
            val decodeByteText = Base64.decode(byteText, Base64.DEFAULT)
            val decryptedByteText = doFinal(decodeByteText)
            Timber.d("KeyStore Test KeyStoreUtils decrypt encodeText : $encodeText, decryptText : ${decryptedByteText.toString(Charsets.UTF_8)}")
            decryptedByteText.toString(Charsets.UTF_8)
        }
    }?: ""
}