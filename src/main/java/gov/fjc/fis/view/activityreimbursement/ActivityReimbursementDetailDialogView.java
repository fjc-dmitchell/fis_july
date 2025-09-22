package gov.fjc.fis.view.activityreimbursement;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.ActivityReimbursement;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.ObjectClass;
import gov.fjc.fis.service.CategoryService;
import gov.fjc.fis.service.ObjectClassService;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route(value = "activity-reimbursement-dialog/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "fis_ActivityReimbursement_dialog.detail")
@ViewDescriptor(path = "activity-reimbursement-detail-dialog-view.xml")
@EditedEntityContainer("activityReimbursementDc")
public class ActivityReimbursementDetailDialogView extends StandardDetailView<ActivityReimbursement> {
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ObjectClassService objectClassService;

    @ViewComponent
    private CollectionLoader<Category> categoriesDl;
    @ViewComponent
    private CollectionLoader<ObjectClass> objectClassesDl;
    @ViewComponent
    private EntityComboBox<Category> categoryField;
    @ViewComponent
    private EntityComboBox<ObjectClass> objectClassField;
    @ViewComponent
    private TypedTextField<BigDecimal> amountField;
    @ViewComponent
    private Paragraph createdByString;

    Appropriation appropriation;
    Category category;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        ActivityReimbursement reimbursement = getEditedEntity();
        appropriation = reimbursement.getActivity().getDivision().getAppropriation();

        if (entityStates.isNew(reimbursement)) {
            if(reimbursement.getObjectClass()!=null) {
//                categoriesDl.load();
//                objectClassesDl.load();
                categoryField.setValue(reimbursement.getObjectClass().getCategory());
//                categoryField.setReadOnly(true);
//                objectClassField.setReadOnly(true);
                amountField.focus();
                amountField.setAutoselect(true);
            }
        } else {
            categoryField.setValue(reimbursement.getObjectClass().getCategory());
            createdByString.setText(reimbursement.getCreatedByString());
            amountField.focus();
            amountField.setAutoselect(true);
        }
        categoriesDl.load();
//            objectClassField.setReadOnly(true);
    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    protected List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        return categoryService.getCategories(appropriation);
    }

    @Install(to = "objectClassesDl", target = Target.DATA_LOADER)
    protected List<ObjectClass> objectClassesDlLoadDelegate(final LoadContext<ObjectClass> loadContext) {
        return objectClassService.getObjectClassesByCategory(category, true);
    }

    @Install(to = "categoryField", subject = "itemLabelGenerator")
    protected Object categoryFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }

    @Install(to = "objectClassField", subject = "itemLabelGenerator")
    protected Object objectClassFieldItemLabelGenerator(final ObjectClass objectClass) {
        return objectClass.getTitleAndCode();
    }

    @Subscribe("categoryField")
    protected void onCategoryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        category = event.getValue();
        checkObjectClass();
    }

    @ViewComponent
    private CollectionContainer<ObjectClass> objectClassesDc;

    private void checkObjectClass() {
        objectClassesDl.load();
        if (objectClassField.getValue() != null) {
            objectClassField.setValue(
                    objectClassesDl.getContainer().getItems().stream()
                            .filter(boc -> boc.getBudgetObjectClass().equals(objectClassField.getValue().getBudgetObjectClass()))
                            .findFirst()
                            .orElse(null)
            );
        }
    }
}