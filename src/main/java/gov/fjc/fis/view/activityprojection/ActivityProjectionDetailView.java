package gov.fjc.fis.view.activityprojection;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.ActivityProjection;
import gov.fjc.fis.entity.ObjectClass;
import gov.fjc.fis.service.ObjectClassService;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
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
    @Autowired
    private ObjectClassService objectClassService;
    @ViewComponent
    private EntityComboBox<ObjectClass> objectClassField;
    @ViewComponent
    private TypedTextField<Object> amountField;
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        ActivityProjection projection = getEditedEntity();
        if (!entityStates.isNew(projection)) {
            createdByString.setText(projection.getCreatedByString());
            objectClassField.setReadOnly(true);
            amountField.focus();
            amountField.setAutoselect(true);
        }
    }

    @Install(to = "objectClassesDl", target = Target.DATA_LOADER)
    protected List<ObjectClass> objectClassesDlLoadDelegate(final LoadContext<ObjectClass> loadContext) {
        ActivityProjection projection = getEditedEntity();
        return objectClassService.getProjectionObjectClasses(projection.getActivity());
    }

    @Install(to = "objectClassField", subject = "itemLabelGenerator")
    protected String objectClassFieldItemLabelGenerator(final ObjectClass objectClass) {
        return objectClass.getTitleAndCode();
    }
}