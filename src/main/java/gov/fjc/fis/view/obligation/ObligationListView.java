package gov.fjc.fis.view.obligation;

import gov.fjc.fis.entity.Obligation;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "obligations", layout = MainView.class)
@ViewController("fis_Obligation.list")
@ViewDescriptor("obligation-list-view.xml")
@LookupComponent("obligationsDataGrid")
@DialogMode(width = "64em")
public class ObligationListView extends StandardListView<Obligation> {
}