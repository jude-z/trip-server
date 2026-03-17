package payment.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationScheduler {
    @Scheduled(cron = "*/1 * * * *")
    public void reconciliation(){

    }
}
