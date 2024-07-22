package gov.fjc.fis.view.positionaction;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.PositionAction;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "positionActions/:id", layout = MainView.class)
@ViewController("fis_PositionAction.detail")
@ViewDescriptor("position-action-detail-view.xml")
@EditedEntityContainer("positionActionDc")
public class PositionActionDetailView extends StandardDetailView<PositionAction> {
}