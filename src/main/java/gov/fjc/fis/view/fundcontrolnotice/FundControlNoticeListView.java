package gov.fjc.fis.view.fundcontrolnotice;

import gov.fjc.fis.entity.FundControlNotice;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "fundControlNotices", layout = MainView.class)
@ViewController("fis_FundControlNotice.list")
@ViewDescriptor("fund-control-notice-list-view.xml")
@LookupComponent("fundControlNoticesDataGrid")
@DialogMode(width = "64em")
public class FundControlNoticeListView extends StandardListView<FundControlNotice> {
}