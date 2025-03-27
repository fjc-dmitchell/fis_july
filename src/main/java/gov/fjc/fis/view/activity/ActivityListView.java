package gov.fjc.fis.view.activity;

import com.vaadin.flow.data.selection.SelectionEvent;
import gov.fjc.fis.entity.Activity;

import gov.fjc.fis.event.SearchGridSelectedItemsEvent;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "activities", layout = MainView.class)
@ViewController("fis_Activity.list")
@ViewDescriptor("activity-list-view.xml")
@LookupComponent("activitiesDataGrid")
@DialogMode(width = "64em")
public class ActivityListView extends StandardListView<Activity> {
    @Autowired
    private UiEventPublisher uiEventPublisher;
    @Autowired
    private SessionData sessionData;
    @ViewComponent
    private CustomSearchFragment searchFragment;
    @ViewComponent
    private DataGrid<Activity> activitiesDataGrid;

    private boolean fjcFoundation = false;

    protected void setFjcFoundation() {
        this.fjcFoundation = true;
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        searchFragment.setFjcFoundation(fjcFoundation);
        searchFragment.setDataGrid(activitiesDataGrid);
    }

    @Subscribe("activitiesDataGrid")
    protected void onActivitiesDataGridSelection(final SelectionEvent<DataGrid<Activity>, Activity> event) {
        sessionData.setAttribute("searchDataGridSize", event.getAllSelectedItems().size());
        uiEventPublisher.publishEvent(new SearchGridSelectedItemsEvent(this, "searchGridChanged"));
    }
}