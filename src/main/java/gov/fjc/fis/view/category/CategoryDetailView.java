package gov.fjc.fis.view.category;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Category;

import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.view.activityprojectionfragment.ActivityProjectionFragment;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "categories/:id", layout = MainView.class)
@ViewController("fis_Category.detail")
@ViewDescriptor("category-detail-view.xml")
@EditedEntityContainer("categoryDc")
public class CategoryDetailView extends StandardDetailView<Category> {
    @Autowired
    private SessionData sessionData;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private ReadOnlyViewsSupport readOnlyViewsSupport;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private Fragments fragments;

    @ViewComponent
    private EntityPicker<Appropriation> appropriationField;
    @ViewComponent
    private TypedTextField<String> masterObjectClassField;
    @ViewComponent
    private VerticalLayout tabBox;
    @ViewComponent
    private VerticalLayout projectionsBox;
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        var category = getEditedEntity();

        if (entityStates.isNew(category)) {
            var appropriation = appropriationService.getBfyEntryAppropriation(sessionData);
            category.setAppropriation(appropriation);
            appropriationField.setReadOnly(true);
            masterObjectClassField.focus();
        } else {
            var appropriation = category.getAppropriation();
            if ((!appropriation.getStatus())) {
                readOnlyViewsSupport.setViewReadOnly(this, true);
            } else {
                appropriationField.setReadOnly(true);
                masterObjectClassField.setReadOnly(true);
            }
            ActivityProjectionFragment fragment = fragments.create(this, ActivityProjectionFragment.class);
            fragment.setEntity(category);
            projectionsBox.add(fragment);
            tabBox.setVisible(true);
        }
        createdByString.setText(category.getCreatedByString());
    }
}