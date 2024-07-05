package gov.fjc.fis.view.fundcontrolnotice;

import gov.fjc.fis.entity.FundControlNotice;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.flowui.view.*;

@Route(value = "fundControlNotices", layout = MainView.class)
@ViewController("fis_FundControlNotice.list")
@ViewDescriptor("fund-control-notice-list-view.xml")
@LookupComponent("fundControlNoticesDataGrid")
@DialogMode(width = "64em")
public class FundControlNoticeListView extends StandardListView<FundControlNotice> {

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