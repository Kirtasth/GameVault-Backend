package com.kirtasth.gamevault.cart.infrastructure.mappers;

import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.ShoppingCartEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartEntityResolver {

    @PersistenceContext
    private EntityManager entityManager;

    @Named( "shoppingCartEntityResolver")
    public ShoppingCartEntity resolveShoppingCartEntity(Long cartId) {
        if (cartId == null) {
            return null;
        }

        return entityManager.getReference(ShoppingCartEntity.class, cartId);
    }

}
