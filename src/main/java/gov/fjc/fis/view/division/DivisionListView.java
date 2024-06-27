package gov.fjc.fis.view.division;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Division;

import gov.fjc.fis.service.DivisionService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.quicksearchcomponent.QuickSearchComponent;
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
    @Autowired
    private SessionData sessionData;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private UiComponents uiComponents;
    @ViewComponent
    private CollectionLoader<Division> divisionsDl;
    @ViewComponent
    private CollectionContainer<Division> divisionsDc;
    private List<Appropriation> fiscalYears;

    @Subscribe
    public void onInit(final InitEvent event) {
        fiscalYears = getBfyFilterField();
        initQuickSearch();
    }

    private void initQuickSearch() {
        QuickSearchComponent quickSearchComponent = uiComponents.create(QuickSearchComponent.class);
        quickSearchComponent.setFiscalYears(fiscalYears);
    }



    /**
     * @return list of Appropriation entities from the budget fiscal year selector
     * ToDo: refactor since this code is now in AppropriationService. What to do about sessionData?
     */
    public List<Appropriation> getBfyFilterField() {
        Appropriation entryBfy = (Appropriation) sessionData.getAttribute("bfyEntry");
        // ToDo - unchecked cast here; should check type and throw exception (it'll never happen)
        List<Appropriation> searchYears = (List<Appropriation>) sessionData.getAttribute("searchYears");
        if (searchYears != null && searchYears.size() > 0) {
            // ToDo: code Comparator class for here and in MainScreen.java
            searchYears.sort((o1, o2) -> {
                String year1 = o1.getBudgetFiscalYear();
                String year2 = o2.getBudgetFiscalYear();
                if (year1 == null) {
                    return 1;
                } else if (year2 == null) {
                    return -1;
                }
                return year2.compareTo(year1);
            });
            return searchYears;
        } else {
            List<Appropriation> entryCollection = new ArrayList<>();
            entryCollection.add(entryBfy);
            return entryCollection;
        }
    }
}