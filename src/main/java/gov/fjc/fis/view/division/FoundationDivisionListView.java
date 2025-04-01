package gov.fjc.fis.view.division;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "foundation-divisions", layout = MainView.class)
@ViewController(id = "fis_Division.foundation-list")
//@ViewDescriptor(path = "foundation-division-list-view.xml")
//@LookupComponent("divisionsDataGrid")
@DialogMode(width = "64em")
public class FoundationDivisionListView extends DivisionListView {
    public FoundationDivisionListView() {
        super();
        super.setFjcFoundation();
    }

    @Override
    public String getPageTitle() {
        return "FJC Foundation: ".concat(super.getPageTitle());
    }
}