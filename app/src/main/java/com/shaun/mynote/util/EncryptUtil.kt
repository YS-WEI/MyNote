import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.shaun.mynote.util.SharedPreferencesUtil
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal


class EncryptUtil {
    private val KEYSTORE_ALIAS = "MY_NOTE"
    private val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private val AES_MODE = "AES/GCM/NoPadding"
    private val RSA_MODE = "RSA/ECB/PKCS1Padding"
    private val keyStore: KeyStore
    private val context: Context

    constructor(context: Context) {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        this.keyStore = keyStore
        this.context = context

        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            Log.d("EncryptUtil", "ALIAS is not Exist")
            generateKey(context)
            genAESKey()
        } else {
            Log.d("EncryptUtil", "ALIAS is Exist")
        }
    }

    private fun generateKey(context: Context) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateRSAKeyAboveApi23()
        } else {
            generateRSAKeyBelowApi23(context)
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun generateRSAKeyAboveApi23() {
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER)
        val keyGenParameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Suppress("DEPRECATION")
    private fun generateRSAKeyBelowApi23(context: Context) {
        val start: Calendar = Calendar.getInstance()
        val end: Calendar = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)
        val spec: KeyPairGeneratorSpec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(KEYSTORE_ALIAS)
                .setSubject(X500Principal("CN=$KEYSTORE_ALIAS"))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER)
        keyPairGenerator.initialize(spec)
        keyPairGenerator.generateKeyPair()
    }


    private fun getRSAPublicKey() : PublicKey  {
        return keyStore.getCertificate(KEYSTORE_ALIAS).publicKey
    }

    private fun getRSAPrivateKey() : PrivateKey  {
        return keyStore.getKey(KEYSTORE_ALIAS, null) as PrivateKey
    }


    /**
     * 加密
     * @param publicKey
     * @param srcBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private fun encryptRSA(plainText: ByteArray): String {
        val publicKey = getRSAPublicKey()
        // Cipher 負責完成加密或解密工作，基於 RSA
        val cipher: Cipher = Cipher.getInstance(RSA_MODE)
        // 根據公鑰，對 Cipher 物件進行初始化
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedByte = cipher.doFinal(plainText)
        return Base64.encodeToString(encryptedByte, Base64.DEFAULT)


    }

    /**
     * 解密
     * @param privateKey
     * @param srcBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private fun decryptRSA(encryptedText: String): ByteArray {
        val privateKey = getRSAPrivateKey()
        // Cipher負責完成加密或解密工作，基於RSA
        val cipher: Cipher = Cipher.getInstance(RSA_MODE)
        // 根據公鑰，對 Cipher 物件進行初始化
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val encryptedBytes: ByteArray = Base64.decode(encryptedText, Base64.DEFAULT)
        return cipher.doFinal(encryptedBytes)
    }


    private fun genAESKey() {
        // Generate AES-Key
        val aesKey = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(aesKey)

        // Generate 12 bytes iv then save to SharedPrefs
        val generated: ByteArray = secureRandom.generateSeed(12)
        Log.d("EncryptUtil", "generated ${String(generated)}")
//        val iv: String = Base64.encodeToString(generated, Base64.DEFAULT)
        val encryptIV = encryptRSA(generated)
        Log.d("EncryptUtil", "encryptIV $encryptIV")
        SharedPreferencesUtil.setIV(context, encryptIV)

        // Encrypt AES-Key with RSA Public Key then save to SharedPrefs
        val encryptAESKey = encryptRSA(aesKey)
        Log.d("EncryptUtil", "encryptedAESKey $encryptAESKey")
        SharedPreferencesUtil.setAESKey(context, encryptAESKey)
    }

    private fun getIV() :ByteArray? {
        val encryptIV = SharedPreferencesUtil.getIV(context)
        Log.d("EncryptUtil", "encryptIV $encryptIV")
        if(!encryptIV.isNullOrEmpty()) {
            return decryptRSA(encryptIV)
        } else {
            return null
        }
    }

    private fun getAESKey(): SecretKeySpec? {
        val encryptedKey = SharedPreferencesUtil.getAESKey(context)
        Log.d("EncryptUtil", "encryptedAESKey $encryptedKey")
        if(!encryptedKey.isNullOrEmpty()) {
            val aesKey = decryptRSA(encryptedKey)
            Log.d("EncryptUtil", "aesKey $aesKey")
            return SecretKeySpec(aesKey, AES_MODE)
        } else {
           return null
        }
    }

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(AES_MODE)


        cipher.init(Cipher.ENCRYPT_MODE, getAESKey(), IvParameterSpec(getIV()))
        // 加密過後的byte
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        // 將byte轉為base64的string編碼
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String? {
        // 將加密過後的Base64編碼格式 解碼成 byte
        val decodedBytes = Base64.decode(encryptedText.toByteArray(), Base64.DEFAULT)
        // 將解碼過後的byte 使用AES解密
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getAESKey(),
            IvParameterSpec(getIV())
        )
        return String(cipher.doFinal(decodedBytes))
    }

}