import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @Author You Jia
 * @Date 8/7/2018 3:31 PM
 */
public class DESEncrypt {
    private final byte[] DESIV = new byte[] { 0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };
    private AlgorithmParameterSpec iv = null;
    private Key key = null;
    private String charset = "utf-8";

    public void encryptUtil(String deSkey, String charset) throws Exception {

        if (StringUtils.isNotBlank(charset)) {

            this.charset = charset;

        }

        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));

        iv = new IvParameterSpec(DESIV);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        key = keyFactory.generateSecret(keySpec);

    }

    public String encode(String deSkey, String charset, String data) throws Exception {

        encryptUtil(deSkey,charset);

        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));

        BASE64Encoder base64Encoder = new BASE64Encoder();

        return base64Encoder.encode(pasByte);

    }

}
