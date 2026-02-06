package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.DeveloperCriteria;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;

public interface DeveloperRepoPort {

    Result<Developer> save(Developer developer);
    Page<Developer> findAll(PageRequest pageRequest, DeveloperCriteria developerCriteria);
}
