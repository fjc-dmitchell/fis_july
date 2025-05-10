package gov.fjc.fis.view.activityprojection;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.*;
import gov.fjc.fis.service.CategoryService;
import gov.fjc.fis.service.ObjectClassService;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "activityProjections/:id", layout = MainView.class)
@ViewController(id = "fis_ActivityProjection.detail")
@ViewDescriptor(path = "activity-projection-detail-view.xml")
@EditedEntityContainer("activityProjectionDc")
public class ActivityProjectionDetailView extends StandardDetailView<ActivityProjection> {
    @Autowired
    private EntityStates entityStates;
    @ViewComponent
    private CollectionLoader<Category> categoriesDl;
    @ViewComponent
    private CollectionLoader<ObjectClass> objectClassesDl;
    @Autowired
    private ObjectClassService objectClassService;
    @Autowired
    private CategoryService categoryService;
    @ViewComponent
    private FormLayout.FormItem categoryFormItem;
    @ViewComponent
    private EntityComboBox<Category> categoryField;
    @ViewComponent
    private EntityComboBox<ObjectClass> objectClassField;
    @ViewComponent
    private TypedTextField<Object> amountField;
    @ViewComponent
    private Paragraph createdByString;

    Appropriation appropriation;
    Activity activity;
    ActivityProjection projection;
    Category category;
    boolean genericProjection;

//    @Subscribe
//    protected void onInitEntity(final InitEntityEvent<ActivityProjection> event) {
//        projection = event.getEntity();
//    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        projection = getEditedEntity();
        activity = projection.getActivity();
        if (entityStates.isNew(projection)) {
            appropriation = activity.getDivision().getAppropriation();
            genericProjection = activity.getGenericProjection();
            categoryFormItem.setVisible(!genericProjection);
            categoriesDl.load();
            objectClassesDl.load();
        } else {
            createdByString.setText(projection.getCreatedByString());
            categoryField.setValue(projection.getObjectClass().getCategory());
            categoryField.setReadOnly(true);
            objectClassField.setReadOnly(true);
            amountField.focus();
            amountField.setAutoselect(true);
        }
    }

    @Subscribe("categoryField")
    protected void onCategoryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        category = event.getValue();
        var boc = objectClassField.getValue();
        if (boc != null && !boc.getCategory().equals(category)) {
            objectClassField.setValue(null);
        }
        objectClassesDl.load();
    }

    @Subscribe("objectClassField")
    protected void onObjectClassFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<ObjectClass>, ObjectClass> event) {
        var boc = event.getValue();
        if (boc != null && !boc.getCategory().equals(category)) {
            categoryField.setValue(boc.getCategory());
        }
    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    protected List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        return categoryService.getCategoriesForBfy(appropriation);
    }

    @Install(to = "objectClassesDl", target = Target.DATA_LOADER)
    protected List<ObjectClass> objectClassesDlLoadDelegate(final LoadContext<ObjectClass> loadContext) {
        return objectClassService.getProjectionObjectClasses(activity, category);
    }

    @Install(to = "categoryField", subject = "itemLabelGenerator")
    protected Object categoryFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }

    @Install(to = "objectClassField", subject = "itemLabelGenerator")
    protected String objectClassFieldItemLabelGenerator(final ObjectClass objectClass) {
        return objectClass.getTitleAndCode();
    }
}