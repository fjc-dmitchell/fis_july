package gov.fjc.fis.view.division;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.ViewController;

@Route(value = "foundation-divisions", layout = MainView.class)
@ViewController("fis_Division.foundation-list")
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