package com.kirtasth.gamevault.catalog.application;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.DeveloperCriteria;
import com.kirtasth.gamevault.catalog.domain.models.NewDeveloper;
import com.kirtasth.gamevault.catalog.domain.ports.in.DeveloperServicePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.DeveloperRepoPort;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeveloperServiceAdapter implements DeveloperServicePort {

    private final DeveloperRepoPort developerRepo;

    @Override
    public Result<Developer> createDeveloper(NewDeveloper newDeveloper) {
        var dev = Developer.builder()
                .name(newDeveloper.name())
                .description(newDeveloper.description())
                .games(List.of())
                .build();

        return developerRepo.save(dev);
    }

    @Override
    public Result<Developer> updateDeveloper(Developer developer) {
        return developerRepo.save(developer);
    }

    @Override
    public Page<Developer> listAll(PageRequest pageRequest, DeveloperCriteria developerCriteria) {
        return developerRepo.findAll(pageRequest, developerCriteria);
    }
}
