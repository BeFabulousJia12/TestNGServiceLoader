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
    private AlgorithmParameterSpec iv = null;// 加密算法的参数接口
    private Key key = null;
    private String charset = "utf-8";
    /**

     * 初始化

     * @param deSkey	密钥

     * @throws Exception

     */

    public void encryptUtil(String deSkey, String charset) throws Exception {

        if (StringUtils.isNotBlank(charset)) {

            this.charset = charset;

        }

        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// 设置密钥参数

        iv = new IvParameterSpec(DESIV);// 设置向量

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂

        key = keyFactory.generateSecret(keySpec);// 得到密钥对象

    }
    //DES加密
    public String encode(String deSkey, String charset, String data) throws Exception {

        encryptUtil(deSkey,charset);

        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher

        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量

        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));

        BASE64Encoder base64Encoder = new BASE64Encoder();

        return base64Encoder.encode(pasByte);

    }

}
