package gov.fjc.fis.listener;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.service.ActivityProjectionService;
import gov.fjc.fis.service.ActivityReimbursementService;
import gov.fjc.fis.service.ObligationService;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("fis_ActivityEventListener")
public class ActivityEventListener {
    @Autowired
    private ObligationService obligationService;
    @Autowired
    private ActivityProjectionService projectionService;
    @Autowired
    private ActivityReimbursementService reimbursementService;

    @EventListener
    public void onActivitySaving(final EntitySavingEvent<Activity> event) {
        // for safety, ensure obligated, projected, and reimbursed amounts are correct on activity update
        if (!event.isNewEntity()) {
            Activity activity = event.getEntity();
            activity.setObligatedAmount(obligationService.sumObligations(activity));
            activity.setProjectedAmount(projectionService.sumProjections(activity));
            activity.setReimbursedAmount(reimbursementService.sumReimbursements(activity));
        }
    }
}