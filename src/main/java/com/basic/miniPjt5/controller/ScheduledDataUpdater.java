package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.service.TMDBDataInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledDataUpdater {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledDataUpdater.class);
    
    private final TMDBDataInitializationService initializationService;
    
    public ScheduledDataUpdater(TMDBDataInitializationService initializationService) {
        this.initializationService = initializationService;
    }
    
    // 매일 새벽 2시에 데이터 업데이트 (크론 표현식)
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateDataDaily() {
        logger.info("정기 데이터 업데이트 시작");
        try {
            initializationService.initializeData();
            logger.info("정기 데이터 업데이트 완료");
        } catch (Exception e) {
            logger.error("정기 데이터 업데이트 실패", e);
        }
    }
}