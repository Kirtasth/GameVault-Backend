package com.kirtasth.gamevault.users.domain.ports.in;

import java.security.Key;

public interface KeyGeneratorPort {

    Key getPublicKey();
    Key getPrivateKey();

}
