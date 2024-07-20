package gov.fjc.fis.view.group;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.Group;

import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.DivisionService;
import gov.fjc.fis.view.activityfragment.ActivityFragment;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.obligationfragment.ObligationFragment;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "groups/:id", layout = MainView.class)
@ViewController("fis_Group.detail")
@ViewDescriptor("group-detail-view.xml")
@EditedEntityContainer("groupDc")
public class GroupDetailView extends StandardDetailView<Group> {
    @Autowired
    private SessionData sessionData;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private ReadOnlyViewsSupport readOnlyViewsSupport;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private Fragments fragments;
    @ViewComponent
    private TypedTextField<String> budgetFiscalYearField;
    @ViewComponent
    private EntityComboBox<Division> divisionsComboBox;
    @ViewComponent
    private TypedTextField<String> groupCodeField;
    @ViewComponent
    private JmixTabSheet tabSheet;
    @ViewComponent
    private Paragraph createdByString;
    @ViewComponent
    private HorizontalLayout activitiesFragment;
    @ViewComponent
    private HorizontalLayout obligationsFragment;
    Appropriation appropriation;
    boolean fjcFoundation;

    public void setFoundation(boolean foundation) {
        this.fjcFoundation = foundation;
    }

    @Subscribe
    protected void onInitEntity(final InitEntityEvent<Group> event) {
        appropriation = (Appropriation) sessionData.getAttribute("bfyEntry");
        budgetFiscalYearField.setValue(appropriation.getBudgetFiscalYear());
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        Group group = getEditedEntity();
        if (entityStates.isNew(group)) {
            appropriation = appropriationService.getBfyEntryAppropriation(sessionData);
            budgetFiscalYearField.setValue(appropriation.getBudgetFiscalYear());
            divisionsComboBox.focus();
            tabSheet.setVisible(false);
        } else {
            appropriation = group.getDivision().getAppropriation();
            budgetFiscalYearField.setValue(appropriation.getBudgetFiscalYear());
            divisionsComboBox.setValue(group.getDivision());
            divisionsComboBox.setReadOnly(true);
            groupCodeField.setReadOnly(true);
            if (!appropriation.getStatus()) {
                readOnlyViewsSupport.setViewReadOnly(this, true);
            }
        }
        createdByString.setText(group.getCreatedByString());

        ActivityFragment fragment = fragments.create(this, ActivityFragment.class);
        fragment.setEntity(group);
        activitiesFragment.add(fragment);

        ObligationFragment obligationFragment = fragments.create(this, ObligationFragment.class);
        obligationFragment.setEntity(group);

        obligationsFragment.add(obligationFragment);
    }

    @Install(to = "divisionsDl", target = Target.DATA_LOADER)
    protected List<Division> divisionsDlLoadDelegate(final LoadContext<Division> loadContext) {
        return divisionService.getDivisions(appropriation, fjcFoundation);
    }

    @Install(to = "divisionsComboBox", subject = "itemLabelGenerator")
    protected Object divisionsComboBoxItemLabelGenerator(final Division division) {
        return division.getTitleAndCode();
    }
}