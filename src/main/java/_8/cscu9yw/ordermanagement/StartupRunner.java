package _8.cscu9yw.ordermanagement;

import _8.cscu9yw.ordermanagement.service.WholesalerSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Marks this class as a Spring component for dependency injection
public class StartupRunner implements CommandLineRunner {

    private final WholesalerSyncService wholesalerSyncService;
    // Constructor for dependency injection of WholesalerSyncService
    public StartupRunner(WholesalerSyncService wholesalerSyncService) {
        this.wholesalerSyncService = wholesalerSyncService;
    }

    @Override
    public void run(String... args) throws Exception {
        wholesalerSyncService.synchronizeProducts(); // Synchronizes products when the app starts
    }
}
