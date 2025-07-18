package gov.fjc.fis.view.obligation;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.*;
import gov.fjc.fis.service.*;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.ComponentUtils;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route(value = "obligations/:id", layout = MainView.class)
@ViewController(id = "fis_Obligation.detail")
@ViewDescriptor(path = "obligation-detail-view.xml")
@EditedEntityContainer("obligationDc")
public class ObligationDetailView extends StandardDetailView<Obligation> {
    @Autowired
    ReadOnlyViewsSupport readOnlyViewsSupport;
    @Autowired
    SessionData sessionData;
    @Autowired
    private EntityStates entityStates;

    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ObjectClassService objectClassService;

    @ViewComponent
    private CollectionLoader<Division> divisionsDl;
    @ViewComponent
    private CollectionLoader<Activity> activitiesDl;
    @ViewComponent
    private CollectionLoader<Category> categoriesDl;
    @ViewComponent
    private CollectionLoader<ObjectClass> objectClassesDl;

    @ViewComponent
    private TypedTextField<String> budgetFiscalYearField;
    @ViewComponent
    private EntityComboBox<Division> divisionField;
    @ViewComponent
    private EntityComboBox<Category> categoryField;
    @ViewComponent
    private EntityComboBox<ObjectClass> budgetObjectClassField;
    @ViewComponent
    private JmixComboBox<Boolean> statusField;
    @ViewComponent
    private JmixComboBox<Boolean> blanketPurchaseOrderField;
    @ViewComponent
    private Paragraph createdByString;

    private Appropriation entryBfy;
    private Division division;
    private Category category;
    private Boolean fjcFoundation = false;
    @ViewComponent
    private EntityComboBox<Activity> activityField;
    @ViewComponent
    private JmixTextArea memoField;

    public void setFjcFoundation(Boolean fjcFoundation) {
        this.fjcFoundation = fjcFoundation;
        if(fjcFoundation) {
            var obligation = getEditedEntity();
            divisionsDl.load();
        }
    }


    @ViewComponent
    private InstanceLoader<Obligation> obligationDl;

    @Subscribe
    protected void onInit(final InitEvent event) {
        ComponentUtils.setItemsMap(statusField, getStatusItemsMap());
        ComponentUtils.setItemsMap(blanketPurchaseOrderField, getBpoItemsMap());
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        entryBfy = appropriationService.getBfyEntryAppropriation(sessionData);
        obligationDl.load();
        var obligation = getEditedEntity();
        if (entityStates.isNew(obligation)) {
//           entryBfy = appropriationService.getBfyEntryAppropriation(sessionData);
            if (entryBfy != null) {
                budgetFiscalYearField.setValue(entryBfy.getBudgetFiscalYear());
            }
//            divisionField.focus();

        } else {
            Appropriation appropriation = obligation.getObjectClass().getCategory().getAppropriation();
            budgetFiscalYearField.setValue(appropriation.getBudgetFiscalYear());

            divisionField.setValue(obligation.getActivity().getDivision());
            categoryField.setValue(obligation.getObjectClass().getCategory());
            budgetObjectClassField.setValue(obligation.getObjectClass());
            activityField.setValue(obligation.getActivity());

            divisionField.setReadOnly(true);
            categoryField.setReadOnly(true);
            budgetFiscalYearField.setReadOnly(true);
            activityField.setReadOnly(true);

//            divisionField.focus();
            createdByString.setText(obligation.getCreatedByString());
        }
        divisionsDl.load();
        categoriesDl.load();
        objectClassesDl.load();
    }

    @Install(to = "divisionsDl", target = Target.DATA_LOADER)
    protected List<Division> divisionsDlLoadDelegate(final LoadContext<Division> loadContext) {
        return divisionService.getDivisions(entryBfy, fjcFoundation);
    }

    @Install(to = "activitiesDl", target = Target.DATA_LOADER)
    protected List<Activity> activitiesDlLoadDelegate(final LoadContext<Activity> loadContext) {
        return activityService.getActivities(division);
    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    protected List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        return categoryService.getCategoriesForBfy(entryBfy);
    }

    @Install(to = "objectClassesDl", target = Target.DATA_LOADER)
    protected List<ObjectClass> objectClassesDlLoadDelegate(final LoadContext<ObjectClass> loadContext) {
        return objectClassService.getObjectClassesByCategory(categoryField.getValue(), false);
    }

    @Install(to = "divisionField", subject = "itemLabelGenerator")
    protected Object divisionFieldItemLabelGenerator(final Division division) {
        return division.getTitleAndCode();
    }

    @Install(to = "categoryField", subject = "itemLabelGenerator")
    protected Object categoryFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }

    @Install(to = "budgetObjectClassField", subject = "itemLabelGenerator")
    protected Object budgetObjectClassFieldItemLabelGenerator(final ObjectClass objectClass) {
        return objectClass.getTitleAndCode();
    }

    @Install(to = "activityField", subject = "itemLabelGenerator")
    protected Object activityFieldItemLabelGenerator(final Activity activity) {
        return activity.getTitleAndCode();
    }

    @Subscribe("divisionField")
    protected void onDivisionFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Division>, Division> event) {
        division = event.getValue();
        checkActivity();
    }

    @Subscribe("categoryField")
    protected void onCategoryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        category = event.getValue();
        checkObjectClass();
    }

    @Subscribe("memoField")
    protected void onMemoFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixTextArea, ?> event) {
       memoField.setValue(((String) event.getValue()).trim());
    }

    private void checkObjectClass() {
        objectClassesDl.load();
        if (budgetObjectClassField.getValue() != null) {
            budgetObjectClassField.setValue(
                    objectClassesDl.getContainer().getItems().stream()
                            .filter(boc -> boc.getBudgetObjectClass().equals(budgetObjectClassField.getValue().getBudgetObjectClass()))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    private void checkActivity() {
        activitiesDl.load();
        if (activityField.getValue() != null) {
            activityField.setValue(
                    activitiesDl.getContainer().getItems().stream()
                            .filter(act -> act.equals(activityField.getValue()))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    protected Map<Boolean, String> getStatusItemsMap() {
        LinkedHashMap<Boolean, String> map = new LinkedHashMap<>();
        map.put(Boolean.TRUE, "Open");
        map.put(Boolean.FALSE, "Closed");
        return map;
    }

    protected Map<Boolean, String> getBpoItemsMap() {
        LinkedHashMap<Boolean, String> map = new LinkedHashMap<>();
        map.put(Boolean.TRUE, "Yes");
        map.put(Boolean.FALSE, "No");
        return map;
    }

    @ViewComponent
    private JmixSelect<Boolean> xxxx;
}