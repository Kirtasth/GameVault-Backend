package com.kirtasth.gamevault.catalog.infrastructure.repositories;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.DeveloperCriteria;
import com.kirtasth.gamevault.catalog.domain.ports.out.DeveloperRepoPort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.DeveloperRepository;
import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeveloperRepoAdapter implements DeveloperRepoPort {

    private final DeveloperRepository developerRepository;
    private final AuthMapper mapper;
    private final PageMapper pageMapper;

    @Override
    public Result<Developer> save(Developer developer) {
        return new Result.Success<>(
                mapper.toDeveloper(developerRepository.save(mapper.toDeveloperEntity(developer)))
        );
    }

    @Override
    public Page<Developer> findAll(PageRequest pageRequest, DeveloperCriteria developerCriteria) {
        var pageable = pageMapper.toSpring(pageRequest);
        Specification<DeveloperEntity> spec = Specification.unrestricted();


        return Page.empty(0,10);
    }
}
