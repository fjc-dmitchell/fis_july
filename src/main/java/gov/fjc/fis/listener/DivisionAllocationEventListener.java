package gov.fjc.fis.listener;

import gov.fjc.fis.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("fis_DivisionAllocationEventListener")
public class DivisionAllocationEventListener {
    @Autowired
    private DataManager dataManager;

    @EventListener
    public void onDivisionAllocationChangedBeforeCommit(final EntityChangedEvent<DivisionAllocation> event) {
        DivisionAllocationAudit audit = dataManager.create(DivisionAllocationAudit.class);
        if (event.getType() == EntityChangedEvent.Type.DELETED) {
            Id<Division> divisionId = event.getChanges().getOldReferenceId("division");
            assert divisionId != null;
            Division division = dataManager.load(divisionId).one();
            audit.setDivision(division);
            Id<Category> categoryId = event.getChanges().getOldReferenceId("category");
            assert categoryId != null;
            Category category = dataManager.load(categoryId).one();
            audit.setCategory(category);
            audit.setOldOneYearAmount(event.getChanges().getOldValue("oneYearAmount"));
            audit.setNewOneYearAmount(null);
            audit.setOldTwoYearAmount(event.getChanges().getOldValue("twoYearAmount"));
            audit.setNewTwoYearAmount(null);

            audit.setChangeType(AuditChangeType.DELETED);
        } else {
            DivisionAllocation allocation = dataManager.load(event.getEntityId()).one();
            audit.setDivision(allocation.getDivision());
            audit.setCategory(allocation.getCategory());

            var changes = event.getChanges().getAttributes().stream()
                    .filter(change -> change.equals("oneYearAmount") || change.equals("twoYearAmount"))
                    .toList().toString();
            audit.setAttributeChanges(changes);

            if (event.getChanges().isChanged("oneYearAmount")) {
                audit.setOldOneYearAmount(event.getChanges().getOldValue("oneYearAmount"));
                audit.setNewOneYearAmount(allocation.getOneYearAmount());
            }
            if (event.getChanges().isChanged("twoYearAmount")) {
                audit.setOldTwoYearAmount(event.getChanges().getOldValue("twoYearAmount"));
                audit.setNewTwoYearAmount(allocation.getTwoYearAmount());
            }

            if (event.getType() == EntityChangedEvent.Type.CREATED) {
                audit.setChangeType(AuditChangeType.CREATED);
            } else {
                audit.setChangeType(AuditChangeType.UPDATED);
            }
        }
        dataManager.save(audit);
    }
}