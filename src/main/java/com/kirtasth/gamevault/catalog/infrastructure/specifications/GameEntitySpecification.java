package com.kirtasth.gamevault.catalog.infrastructure.specifications;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameTagEntity;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameEntitySpecification {

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
            if (developer == null || developer.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            if (query != null) {
                query.distinct(true);
            }
            Join<GameEntity, DeveloperEntity> join = root.join("developer", JoinType.INNER);

            return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")),
                    "%" + developer.toLowerCase() + "%");
        });
    }

    public Specification<GameEntity> priceGreaterOrEqual(Double price) {
        return ((root, query, criteriaBuilder) ->
                price == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price)
        );
    }

    public Specification<GameEntity> priceLessOrEqual(Double price) {
        return ((root, query, criteriaBuilder) ->
                price == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price)
        );
    }

    public Specification<GameEntity> releasedAfter(Instant releaseDate) {
        return ((root, query, criteriaBuilder) ->
                releaseDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), releaseDate)
        );
    }

    public Specification<GameEntity> releasedBefore(Instant releaseDate) {
        return ((root, query, criteriaBuilder) ->
                releaseDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), releaseDate)
        );
    }

    public Specification<GameEntity> containsAllGameTags(List<String> gameTags) {
        return ((root, query, criteriaBuilder) -> {
            if (gameTags == null || gameTags.isEmpty() || query == null) {
                return criteriaBuilder.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<GameEntity> subRoot = subquery.from(GameEntity.class);
            Join<GameEntity, GameTagEntity> subJoin = subRoot.join("tags");
            subquery.select(subRoot.get("id"))
                    .where(subJoin.get("name").in(gameTags))
                    .groupBy(subRoot.get("id"))
                    .having(criteriaBuilder.equal(criteriaBuilder.count(subJoin), (long) gameTags.size()));

            return root.get("id").in(subquery);
        });
    }

    public Specification<GameEntity> idsIn(List<Long> gameIds) {
        return (root, query, criteriaBuilder) -> {
            if (gameIds == null || gameIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("id").in(gameIds);
        };
    }

    public Specification<GameEntity> hasAvailableKeys(Instant now) {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                return criteriaBuilder.conjunction();
            }
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<GameKeyEntity> subRoot = subquery.from(GameKeyEntity.class);
            subquery.select(subRoot.get("game").get("id"))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(subRoot.get("game"), root),
                                    criteriaBuilder.isNull(subRoot.get("purchasedByUserId")),
                                    criteriaBuilder.or(
                                            criteriaBuilder.isNull(subRoot.get("reservedByUserId")),
                                            criteriaBuilder.lessThan(subRoot.get("reservedDeadline"), now)
                                    )
                            )
                    );
            return criteriaBuilder.exists(subquery);
        };
    }
}
