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
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    private Paragraph createdByString;

    private Appropriation entryBfy;
    private Division division;
    private Category category;
    private Boolean foundation = false;

    public void setFoundation(Boolean foundation) {
        this.foundation = foundation;
    }

    @Subscribe
    protected void onInit(final InitEvent event) {
        entryBfy = appropriationService.getBfyEntryAppropriation(sessionData);
    }

    @ViewComponent
    private InstanceLoader<Obligation> obligationDl;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        obligationDl.load();
        var obligation = getEditedEntity();
        if (entityStates.isNew(obligation)) {
//           entryBfy = appropriationService.getBfyEntryAppropriation(sessionData);
            if (entryBfy != null) {
                budgetFiscalYearField.setValue(entryBfy.getBudgetFiscalYear());
            }
            divisionsDl.load();
            categoriesDl.load();
            objectClassesDl.load();
            divisionField.focus();

        } else {
            divisionsDl.load();
            categoriesDl.load();
            objectClassesDl.load();
            divisionField.focus();
            createdByString.setText(obligation.getCreatedByString());
        }
    }

    @Install(to = "divisionsDl", target = Target.DATA_LOADER)
    protected List<Division> divisionsDlLoadDelegate(final LoadContext<Division> loadContext) {
        return divisionService.getDivisions(entryBfy, foundation);
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
        activitiesDl.load();
    }

    @Subscribe("categoryField")
    protected void onCategoryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
       category =  event.getValue();
       // clear object class or keep, depending on value
       objectClassesDl.load();
    }


}