package com.sky.api.weatherapiservice.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRemovalScheduledTask {
    private final RefreshTokenRepository repo;

    @Scheduled(fixedRateString = "${app.security.jwt.refresh-token-removal}"
            , initialDelay = 5000)
    @Transactional
    public void deleteExpiredRefreshToken(){
        int tokenDeleted=repo.deleteByExpiryTime();
        log.info("Number of expired refresh tokens deleted: "+tokenDeleted);
    }

}
