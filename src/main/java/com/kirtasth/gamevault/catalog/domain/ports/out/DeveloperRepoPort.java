package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.application.exception.DeveloperAlreadyExistsException;
import com.kirtasth.gamevault.catalog.domain.models.Developer;

public interface DeveloperRepoPort {

    Developer save(Developer developer) throws DeveloperAlreadyExistsException;
}
