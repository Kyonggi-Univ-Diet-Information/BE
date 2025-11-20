package com.kyonggi.diet.elasticsearch.runner;

import com.kyonggi.diet.elasticsearch.service.MenuSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialSyncRunner implements CommandLineRunner {

    private final MenuSyncService syncService;

    public InitialSyncRunner(MenuSyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void run(String... args) {
        syncService.syncAll();
        System.out.println("Menu synced to Elasticsearch!");
    }
}
