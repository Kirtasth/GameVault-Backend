package com.kirtasth.gamevault.catalog.infrastructure.specifications;

import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameTagEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameEntitySpecification {

    private final UserValidationPort userValidation;

    public Specification<GameEntity> containsTitle(String title) {
        return ((root, query, criteriaBuilder) ->
                title == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%")
        );
    }

    public Specification<GameEntity> containsDeveloper(String developer) {


        return ((root, query, criteriaBuilder) -> {
            if (developer == null || developer.isBlank() || query == null) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);
            Join<GameEntity, DeveloperEntity> join = root.join("developers", JoinType.INNER);


            return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")),
                    "%" + developer.toLowerCase() + "%");
        }
        );
    }

    public Specification<GameEntity> prizeGreaterOrEqual(Double prize) {
        return ((root, query, criteriaBuilder) ->
                prize == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("prize"), prize)
        );
    }

    public Specification<GameEntity> prizeLessOrEqual(Double prize) {
        return ((root, query, criteriaBuilder) ->
                prize == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("prize"), prize)
        );

    }

    public Specification<GameEntity> containsAllGameTags(List<String> gameTags) {
        return ((root, query, criteriaBuilder) -> {
            if (gameTags == null || gameTags.isEmpty() || query == null) {
                return criteriaBuilder.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<GameEntity> subRoot = subquery.from(GameEntity.class);
            Join<GameEntity, GameTagEntity> subJoin = subRoot.join("gameTags");
            subquery.select(subRoot.get("id"))
                    .where(subJoin.get("name").in(gameTags))
                    .groupBy(subRoot.get("id"))
                    .having(criteriaBuilder.equal(criteriaBuilder.count(subJoin), (long) gameTags.size()));

            return root.get("id").in(subquery);
        });
    }
}
