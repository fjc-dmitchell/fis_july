package gov.fjc.fis.view.division;

import com.vaadin.flow.component.html.Paragraph;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Division;

import gov.fjc.fis.entity.DivisionAllocation;
import gov.fjc.fis.service.FundService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Route(value = "divisions/:id", layout = MainView.class)
@ViewController("fis_Division.detail")
@ViewDescriptor("division-detail-view.xml")
@EditedEntityContainer("divisionDc")
public class DivisionDetailView extends StandardDetailView<Division> {
    @Autowired
    private SessionData sessionData;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private ReadOnlyViewsSupport readOnlyViewsSupport;
    @Autowired
    private FundService fundService;
    @ViewComponent
    private TypedTextField<Character> divisionCodeField;
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Division> event) {
        var appropriation = (Appropriation) sessionData.getAttribute("bfyEntry");
        event.getEntity().setAppropriation(appropriation);
        event.getEntity().setFund(fundService.getAppropriationOneYearFund());
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        var division = getEditedEntity();
        sortAllocations(division.getAllocations());
        createdByString.setText(division.getCreatedByString());
        if (entityStates.isNew(division)) {
            divisionCodeField.setReadOnly(false);
        } else {
            divisionCodeField.setReadOnly(true);
            var appropriation = division.getAppropriation();
            if (!appropriation.getStatus()) {
                readOnlyViewsSupport.setViewReadOnly(this, true);
            }
        }
    }

    /**
     * when opening a Division detail view, sequence the allocations in MOC order. Jmix doesn't
     * allow sorting by category.masterObjectClass in the Division entity.
     *
     * @param allocations
     */
    private void sortAllocations(List<DivisionAllocation> allocations) {
        if (allocations != null) {
            allocations.sort(Comparator.comparing(o -> o.getCategory().getMasterObjectClass()));
        }
    }

    // KEEP THIS COMMENTED OUT UNTIL AGGREGATION BUG IS FIXED!
//    @Subscribe(id = "allocationsDc", target = Target.DATA_CONTAINER)
//    public void onAllocationsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<DivisionAllocation> event) {
//        allocationWarning();
//    }
//
//    @Subscribe("totalAmountField")
//    public void onTotalAmountFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedTextField<?>, ?> event) {
//        allocationWarning();
//    }
//
//    private void allocationWarning() {
//        var division = getEditedEntity();
//        var oneYearAlloc = (BigDecimal) allocationsDataGrid.getAggregationResults().get(allocationsDataGrid.getColumnByKey("oneYearAmount"));
//        var twoYearAlloc = (BigDecimal) allocationsDataGrid.getAggregationResults().get(allocationsDataGrid.getColumnByKey("twoYearAmount"));
//
//        allocWarning.setVisible((division.getOneYearAmount().compareTo(oneYearAlloc) != 0) || (division.getTwoYearAmount().compareTo(twoYearAlloc) != 0));
//    }


}