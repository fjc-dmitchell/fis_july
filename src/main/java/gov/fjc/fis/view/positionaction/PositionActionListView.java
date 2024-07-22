package gov.fjc.fis.view.positionaction;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.PositionAction;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "positionActions", layout = MainView.class)
@ViewController("fis_PositionAction.list")
@ViewDescriptor("position-action-list-view.xml")
@LookupComponent("positionActionsDataGrid")
@DialogMode(width = "64em")
public class PositionActionListView extends StandardListView<PositionAction> {
}