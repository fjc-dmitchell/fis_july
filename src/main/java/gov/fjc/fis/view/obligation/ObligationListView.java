package gov.fjc.fis.view.obligation;

import com.vaadin.flow.data.selection.SelectionEvent;
import gov.fjc.fis.entity.Obligation;

import gov.fjc.fis.event.SearchGridSelectedItemsEvent;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "obligations", layout = MainView.class)
@ViewController("fis_Obligation.list")
@ViewDescriptor("obligation-list-view.xml")
@LookupComponent("obligationsDataGrid")
@DialogMode(width = "64em")
public class ObligationListView extends StandardListView<Obligation> {
    @Autowired
    private UiEventPublisher uiEventPublisher;
    @Autowired
    private SessionData sessionData;
    @ViewComponent
    private CustomSearchFragment searchFragment;
    @ViewComponent
    private DataGrid<Obligation> obligationsDataGrid;

    private boolean fjcFoundation = false;

    protected void setFjcFoundation() {
        this.fjcFoundation = true;
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        searchFragment.setFjcFoundation(fjcFoundation);
        searchFragment.setDataGrid(obligationsDataGrid);
    }

    @Subscribe("obligationsDataGrid")
    protected void onObligationsDataGridSelection(final SelectionEvent<DataGrid<Obligation>, Obligation> event) {
        sessionData.setAttribute("searchDataGridSize", event.getAllSelectedItems().size());
        uiEventPublisher.publishEvent(new SearchGridSelectedItemsEvent(this, "searchGridChanged"));
    }
}