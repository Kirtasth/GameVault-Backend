package com.kirtasth.gamevault.auth.application.jwt;

import com.kirtasth.gamevault.auth.domain.ports.in.KeyGeneratorPort;
import com.kirtasth.gamevault.auth.domain.ports.out.FileSystemPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtKeyGenerator implements KeyGeneratorPort {

    private final String jwtKeysPath = "jwt-keys";
    private final String publicJwtKeysPath = jwtKeysPath + "/public.key";
    private final String privateJwtKeysPath = jwtKeysPath + "/private.key";

    private KeyPair accessTokenKeyPair;

    private final FileSystemPort fileSystem;

    @Override
    public Key getPublicKey() {
        return this.getAccessTokenKeyPair().getPublic();
    }

    @Override
    public Key getPrivateKey() {
        return this.getAccessTokenKeyPair().getPrivate();
    }

    private KeyPair getAccessTokenKeyPair() {
        if (this.accessTokenKeyPair == null) {
            this.accessTokenKeyPair = this.getKeyPair();
        }
        return this.accessTokenKeyPair;
    }

    private KeyPair getKeyPair() {
        KeyPair keyPair;

        File publicKeyFile = this.fileSystem.newFile(this.publicJwtKeysPath);
        File privateKeyFile = this.fileSystem.newFile(this.privateJwtKeysPath);

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            log.info("Loading keys from file: {}, {}", publicKeyFile, privateKeyFile);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                byte[] publicKeyBytes = this.fileSystem.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                byte[] privateKeyBytes = this.fileSystem.readAllBytes(privateKeyFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                return keyPair;

            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        File directory = this.fileSystem.newFile(jwtKeysPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            log.info("Generating new public and private keys: {}, {}", publicKeyFile, privateKeyFile);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

            try (FileOutputStream fos = new FileOutputStream(this.publicJwtKeysPath)) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            try (FileOutputStream fos = new FileOutputStream(this.privateJwtKeysPath)) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }

}
