package gov.fjc.fis.view.actioncode;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.ActionCode;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "actionCodes/:id", layout = MainView.class)
@ViewController("fis_ActionCode.detail")
@ViewDescriptor("action-code-detail-view.xml")
@EditedEntityContainer("actionCodeDc")
public class ActionCodeDetailView extends StandardDetailView<ActionCode> {
}