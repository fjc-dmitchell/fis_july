package gov.fjc.fis.view.position;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.Position;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "positions/:id", layout = MainView.class)
@ViewController("fis_Position.detail")
@ViewDescriptor("position-detail-view.xml")
@EditedEntityContainer("positionDc")
public class PositionDetailView extends StandardDetailView<Position> {
}