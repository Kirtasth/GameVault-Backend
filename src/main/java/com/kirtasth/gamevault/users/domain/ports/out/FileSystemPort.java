package com.kirtasth.gamevault.users.domain.ports.out;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileSystemPort {

    File newFile(String path);
    byte[] readAllBytes(Path path) throws IOException;
}
