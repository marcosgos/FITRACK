package com.marcos.fittrack.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.marcos.fittrack.BuildConfig
import java.security.SecureRandom

/**
 * Punto único para pedir el ID token de Google vía Credential Manager,
 * usado tanto desde LoginActivity como desde RegisterActivity: la API
 * decide en /auth/google si es alta nueva o login (ver FitrackAPI.py).
 */
object GoogleAuthHelper {

    /** El usuario cerró el selector de cuentas sin elegir ninguna. */
    class CancelledException : Exception()

    suspend fun obtenerIdToken(context: Context): String {
        val nonce = ByteArray(16).also(SecureRandom()::nextBytes)
            .joinToString("") { "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = try {
            CredentialManager.create(context).getCredential(context, request)
        } catch (e: GetCredentialCancellationException) {
            throw CancelledException()
        }

        val credential = response.credential
        require(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Credencial inesperada: ${credential.type}"
        }

        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
}
