package com.kirtasth.gamevault.auth.domain.ports.in;

import java.security.Key;

public interface KeyGeneratorPort {

    Key getPublicKey();
    Key getPrivateKey();

}
