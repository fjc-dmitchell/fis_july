package gov.fjc.fis.view.bonusprojection;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.BonusProjection;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "bonusProjections/:id", layout = MainView.class)
@ViewController("fis_BonusProjection.detail")
@ViewDescriptor("bonus-projection-detail-view.xml")
@EditedEntityContainer("bonusProjectionDc")
public class BonusProjectionDetailView extends StandardDetailView<BonusProjection> {
}