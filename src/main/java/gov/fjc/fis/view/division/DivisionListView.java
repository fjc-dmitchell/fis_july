package gov.fjc.fis.view.division;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Division;

import gov.fjc.fis.service.DivisionService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.quicksearchcomponent.QuickSearchComponent;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route(value = "divisions", layout = MainView.class)
@ViewController("fis_Division.list")
@ViewDescriptor("division-list-view.xml")
@LookupComponent("divisionsDataGrid")
@DialogMode(width = "64em")
public class DivisionListView extends StandardListView<Division> {

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