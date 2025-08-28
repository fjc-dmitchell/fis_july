package gov.fjc.fis.view.actioncode;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.ActionCode;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "actionCodes/:id", layout = MainView.class)
@ViewController("fis_ActionCode.detail")
@ViewDescriptor("action-code-detail-view.xml")
@EditedEntityContainer("actionCodeDc")
public class ActionCodeDetailView extends StandardDetailView<ActionCode> {
    @Autowired
    private EntityStates entityStates;
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        var code = getEditedEntity();
        if(entityStates.isNew(code)) {

        } else {
            createdByString.setText(code.getCreatedByString());
        }
    }
}