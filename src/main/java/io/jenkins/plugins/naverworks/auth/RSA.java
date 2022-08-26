package io.jenkins.plugins.naverworks.auth;

import org.apache.commons.codec.binary.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

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

}
