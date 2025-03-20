package gov.fjc.fis.view.division;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.view.main.MainView;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.flowui.view.*;


@Route(value = "divisions", layout = MainView.class)
@ViewController(id = "fis_Division.list")
@ViewDescriptor(path = "division-list-view.xml")
@LookupComponent("divisionsDataGrid")
@DialogMode(width = "64em")
public class DivisionListView extends StandardListView<Division> {
    @ViewComponent
    private CustomSearchFragment searchFragment;

    private boolean fjcFoundation = false;

    public void setFjcFoundation() {
        this.fjcFoundation = true;
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        searchFragment.setFjcFoundation(fjcFoundation);
    }
}