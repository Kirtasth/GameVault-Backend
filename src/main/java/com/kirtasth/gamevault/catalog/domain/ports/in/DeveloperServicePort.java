package com.kirtasth.gamevault.catalog.domain.ports.in;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.DeveloperCriteria;
import com.kirtasth.gamevault.catalog.domain.models.NewDeveloper;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;

public interface DeveloperServicePort {

    Result<Developer> createDeveloper(NewDeveloper newDeveloper);
    Result<Developer> updateDeveloper(Developer developer);
    Page<Developer> listAll(PageRequest pageRequest, DeveloperCriteria developerCriteria);
}
