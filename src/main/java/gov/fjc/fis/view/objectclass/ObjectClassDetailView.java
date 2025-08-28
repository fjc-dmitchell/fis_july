package gov.fjc.fis.view.objectclass;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.ObjectClass;

import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.CategoryService;
import gov.fjc.fis.view.activityprojectionfragment.ActivityProjectionFragment;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "objectClasses/:id", layout = MainView.class)
@ViewController("fis_ObjectClass.detail")
@ViewDescriptor("object-class-detail-view.xml")
@EditedEntityContainer("objectClassDc")
public class ObjectClassDetailView extends StandardDetailView<ObjectClass> {
    @Autowired
    private SessionData sessionData;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private ReadOnlyViewsSupport readOnlyViewsSupport;
    @Autowired
    private Notifications notifications;

    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private Fragments fragments;

    @ViewComponent
    private CollectionLoader<Category> categoriesDl;

    @ViewComponent
    private EntityPicker<Appropriation> appropriationField;
    @ViewComponent
    private EntityComboBox<Category> categoryField;
    @ViewComponent
    private TypedTextField<Object> budgetObjectClassField;
    @ViewComponent
    private VerticalLayout tabBox;
    @ViewComponent
    private VerticalLayout projectionsBox;
    @ViewComponent
    private Paragraph createdByString;

    private Appropriation appropriation;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        var objectClass = getEditedEntity();

        if (entityStates.isNew(objectClass)) {
            appropriation = appropriationService.getBfyEntryAppropriation(sessionData);
            categoriesDl.load();
            categoryField.focus();
            appropriationField.setValue(appropriation);
        } else {
            appropriation = objectClass.getCategory().getAppropriation();
            if ((!appropriation.getStatus())) {
                readOnlyViewsSupport.setViewReadOnly(this, true);
            } else {
                categoryField.setReadOnly(true);
                budgetObjectClassField.setReadOnly(true);
            }
            ActivityProjectionFragment fragment = fragments.create(this, ActivityProjectionFragment.class);
            fragment.setEntity(objectClass);
            projectionsBox.add(fragment);
            tabBox.setVisible(true);
        }
//        appropriationField.setValue(appropriation.getBudgetFiscalYear());
        createdByString.setText(objectClass.getCreatedByString());
    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    protected List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        return categoryService.getCategories(appropriation);
    }

    @Install(to = "categoryField", subject = "itemLabelGenerator")
    protected Object categoryFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }

    @Subscribe("categoryField")
    protected void onCategoryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        checkObjectClass();
    }

    @Subscribe("budgetObjectClassField")
    protected void onBudgetObjectClassFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedTextField<?>, ?> event) {
        checkObjectClass();
    }

    private void checkObjectClass() {
        var category = categoryField.getValue();

        if (category != null) {
            var moc = category.getMasterObjectClass();
            String boc;
            if (entityStates.isNew(getEditedEntity())) {
                boc = budgetObjectClassField.getValue();
            } else {
                boc = getEditedEntity().getBudgetObjectClass();
            }
            if (moc != null) {
                if (boc != null && boc.length() == 4) {
                    if (!boc.substring(0, 2).equals(moc)) {
                        notifications.create("Budget Object Class must start with ".concat(moc))
                                .withThemeVariant(NotificationVariant.LUMO_ERROR)
                                .show();
                        budgetObjectClassField.setValue(moc);
                        budgetObjectClassField.focus();
                    }
                } else {
                    budgetObjectClassField.setValue(moc);
                    budgetObjectClassField.focus();
                }
            }
        }
    }
}