package com.uiowa_facial_paralysis.facialparalysisapp;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//Straight copied
// https://stackoverflow.com/questions/41223937/how-can-i-encrypte-my-password-android-studio
//Todo:: how does this work? lmao.
//Todo:: why do we need these exceptions?
public class AESCrypt
{
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";

    public static String encrypt(String value)
    {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
            String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
            return encryptedValue64;
        }
        catch (Exception e)
        {

        }
        return null;
    }

    public static String decrypt(String value)
    {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
            byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
            String decryptedValue = new String(decryptedByteValue, "utf-8");
            return decryptedValue;
        }
        catch (Exception e)
        {

        }
        return null;
    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(AESCrypt.KEY.getBytes(),AESCrypt.ALGORITHM);
        return key;
    }
}
