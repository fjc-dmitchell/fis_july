package gov.fjc.fis.listener;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.ActivityReimbursement;
import gov.fjc.fis.service.ActivityReimbursementService;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("fis_ActivityReimbursementEventListener")
public class ActivityReimbursementEventListener {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private ActivityReimbursementService activityReimbursementService;

    @EventListener
    public void onActivityReimbursementChangedBeforeCommit(final EntityChangedEvent<ActivityReimbursement> event) {
        Activity activity;
        if (event.getType() != EntityChangedEvent.Type.DELETED) {
            Id<ActivityReimbursement> reimbursementId = event.getEntityId();
            ActivityReimbursement reimbursement = dataManager.load(reimbursementId).one();
            activity = reimbursement.getActivity();
        } else {
            Id<Activity> activityId = event.getChanges().getOldValue("activity");
            if (activityId == null) {
                throw new IllegalStateException("Cannot get Activity from deleted reimbursement");
            }
            activity = dataManager.load(activityId).one();
        }

        activity.setReimbursedAmount(activityReimbursementService.sumReimbursements(activity));
        dataManager.save(activity);
    }
}