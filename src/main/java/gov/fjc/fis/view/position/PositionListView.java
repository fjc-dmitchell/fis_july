package gov.fjc.fis.view.position;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.Position;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "positions", layout = MainView.class)
@ViewController("fis_Position.list")
@ViewDescriptor("position-list-view.xml")
@LookupComponent("positionsDataGrid")
@DialogMode(width = "64em")
public class PositionListView extends StandardListView<Position> {
}