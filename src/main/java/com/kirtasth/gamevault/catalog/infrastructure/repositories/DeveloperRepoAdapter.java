package com.kirtasth.gamevault.catalog.infrastructure.repositories;

import com.kirtasth.gamevault.catalog.application.exception.DeveloperAlreadyExistsException;
import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.ports.out.DeveloperRepoPort;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeveloperRepoAdapter implements DeveloperRepoPort {

    private final DeveloperRepository developerRepository;
    private final CatalogMapper mapper;

    @Override
    public Developer save(Developer developer) {
        try {
            return mapper.toDeveloper(developerRepository.save(mapper.toDeveloperEntity(developer)));
        } catch (DataIntegrityViolationException e) {
            throw new DeveloperAlreadyExistsException(developer.name());
        }
    }

}
