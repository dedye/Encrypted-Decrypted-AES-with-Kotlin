import org.apache.commons.codec.digest.DigestUtils
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException
import javax.crypto.spec.SecretKeySpec


class AESEncryptor {

    fun encrypt(value: String, secretKey: String): String? {
        return try {
            val algorithm = "AES"
            val secretKeySha256Byte = DigestUtils.sha256(secretKey.toByteArray(StandardCharsets.UTF_8))
            val key: Key = SecretKeySpec(secretKeySha256Byte, algorithm)
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedValueByte = cipher.doFinal(value.toByteArray())
            return String(
                Base64.encode(encryptedValueByte)
            )
        } catch (ignored: Exception) {
            null
        }
    }

    fun decrypt(key: String, strToDecrypt: String?): String? {
        try {
            synchronized(Cipher::class.java) {
                val algorithm = "AES"
                val secretKeySha256Byte: ByteArray = DigestUtils.sha256(
                    key.toByteArray(charset("UTF8"))
                )

                val key: Key = SecretKeySpec(secretKeySha256Byte, algorithm)
                val cipher = Cipher.getInstance(algorithm)
                val input = Base64.decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

                cipher.init(Cipher.DECRYPT_MODE, key)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}