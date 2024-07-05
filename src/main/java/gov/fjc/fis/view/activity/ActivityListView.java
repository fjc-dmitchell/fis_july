package gov.fjc.fis.view.activity;

import gov.fjc.fis.entity.Activity;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.flowui.view.*;

@Route(value = "activities", layout = MainView.class)
@ViewController("fis_Activity.list")
@ViewDescriptor("activity-list-view.xml")
@LookupComponent("activitiesDataGrid")
@DialogMode(width = "64em")
public class ActivityListView extends StandardListView<Activity> {
    @ViewComponent
    private CustomSearchFragment searchFragment;

    private boolean fjcFoundation = false;

    protected void setFjcFoundation() {
        this.fjcFoundation = true;
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        searchFragment.setFjcFoundation(fjcFoundation);
    }
}