package com.kirtasth.gamevault.checkout.domain.ports.in;

import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.responses.PurchasedGameKeyResponse;
import java.util.List;

public interface ManageGameKeysUseCase {
    void uploadKeys(Long developerId, Long gameId, List<String> keys);
    void markKeyAsUsed(Long developerId, Long keyId);
    List<GameKey> getKeysByGameId(Long developerId, Long gameId);
    List<PurchasedGameKeyResponse> getPurchasedKeys(Long userId);
}
