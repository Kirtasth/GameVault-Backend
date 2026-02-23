package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.application.exception.DeveloperAlreadyExistsException;
import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.DeveloperCriteria;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;

public interface DeveloperRepoPort {

    Developer save(Developer developer) throws DeveloperAlreadyExistsException;
    Page<Developer> findAll(PageRequest pageRequest, DeveloperCriteria developerCriteria);
}
