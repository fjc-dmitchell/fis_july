package gov.fjc.fis.view.fundcontrolnotice;

import gov.fjc.fis.entity.FundControlNotice;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.search.CustomSearchFragment;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "fundControlNotices", layout = MainView.class)
@ViewController("fis_FundControlNotice.list")
@ViewDescriptor("fund-control-notice-list-view.xml")
@LookupComponent("fundControlNoticesDataGrid")
@DialogMode(width = "64em")
public class FundControlNoticeListView extends StandardListView<FundControlNotice> {
    @Autowired
    private ViewNavigators viewNavigators;
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

    @Subscribe("fundControlNoticesDataGrid.create")
    protected void onFundControlNoticesDataGridCreate(final ActionPerformedEvent event) {
        viewNavigators.detailView(this, FundControlNotice.class)
                .withViewClass(FundControlNoticeDetailView.class)
                .withAfterNavigationHandler(afterNavigationEvent -> {
                    FundControlNoticeDetailView view = afterNavigationEvent.getView();
                    view.setFjcFoundation(fjcFoundation);
                })
                .newEntity()
                .navigate();
    }
}