package gov.fjc.fis.listener;

import gov.fjc.fis.entity.*;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import io.jmix.core.DataManager;

@Component
public class ActivityProjectionEventListener {
    @Autowired
    private DataManager dataManager;

    @EventListener
    void onActivityProjectionChangeBeforeCommit(EntityChangedEvent<ActivityProjection> event) {
        ActivityProjectionAudit audit = dataManager.create(ActivityProjectionAudit.class);
        if (event.getType() == EntityChangedEvent.Type.DELETED) {
            Id<Activity> activityId = event.getChanges().getOldReferenceId("activity");
            assert activityId != null;
            Activity activity = dataManager.load(activityId).one();
            audit.setActivity(activity);

            Id<ObjectClass> objectClassId = event.getChanges().getOldReferenceId("objectClass");
            assert objectClassId != null;
            ObjectClass objectClass = dataManager.load(objectClassId).one();
            audit.setObjectClass(objectClass);

            audit.setOldAmount(event.getChanges().getOldValue("amount"));
            audit.setNewAmount(null);
            audit.setChangeType(AuditChangeType.DELETED);
        } else {
            ActivityProjection projection = dataManager.load(event.getEntityId()).one();
            audit.setActivity(projection.getActivity());
            audit.setObjectClass(projection.getObjectClass());

            audit.setOldAmount(event.getChanges().getOldValue("amount"));
            audit.setNewAmount(projection.getAmount());
            if (event.getType() == EntityChangedEvent.Type.CREATED) {
                audit.setChangeType(AuditChangeType.CREATED);
            } else {
                audit.setChangeType(AuditChangeType.UPDATED);
            }
        }
        dataManager.save(audit);
    }
}