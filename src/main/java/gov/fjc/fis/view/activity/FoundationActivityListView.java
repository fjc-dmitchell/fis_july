package gov.fjc.fis.view.activity;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.ViewController;

@Route(value = "foundation-activities", layout = MainView.class)
@ViewController("fis_Activity.foundation-list")
public class FoundationActivityListView extends ActivityListView {
    public FoundationActivityListView() {
        super();
        super.setFjcFoundation();
    }
}
