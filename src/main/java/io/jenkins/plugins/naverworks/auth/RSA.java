package io.jenkins.plugins.naverworks.auth;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 암호화
 */
public class RSA {

    private RSA() {
        // Utility Class
    }

    /**
     * Private Key를 읽는다.
     * *
     *
     * @param privateKey Private Key 내용
     * @return Private Key
     * @throws GeneralSecurityException
     */
    public static RSAPrivateKey getPrivateKey(String privateKey)
            throws GeneralSecurityException {
        return getPrivateKeyFromString(privateKey);
    }

    private static RSAPrivateKey getPrivateKeyFromString(String key)
            throws GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) rsaKeyFactory.generatePrivate(keySpec);
    }

    /**
     * Public Key를 읽는다.
     *
     * @param path Public Key 파일 경로
     * @return Public Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static RSAPublicKey getPublicKey(String path)
            throws IOException, GeneralSecurityException {
        String publicKeyPEM = getKey(path);
        return getPublicKeyFromString(publicKeyPEM);
    }

    private static RSAPublicKey getPublicKeyFromString(String key)
            throws GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
    }


    /**
     * 파일 경로에서 Key 파일을 읽는다.
     *
     * @param path 파일 경로
     * @return Key
     * @throws IOException 파일을 읽을 때 문제가 발생할 경우
     */
    private static String getKey(String path)
            throws IOException {
        final ClassPathResource resource = new ClassPathResource(path);
        InputStream inputStream = resource.getInputStream();
        byte[] binData = FileCopyUtils.copyToByteArray(inputStream);
        return new String(binData, StandardCharsets.UTF_8);
    }

}
