package com.kirtasth.gamevault.auth.infrastructure.filesystem;

import com.kirtasth.gamevault.auth.domain.ports.out.FileSystemPort;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileSystemAdapter implements FileSystemPort {

    @Override
    public File newFile(String path) {
        return new File(path);
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
