package gov.fjc.fis.view.fundcontrolnotice;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.FundControlNotice;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "fundControlNotices/:id", layout = MainView.class)
@ViewController("fis_FundControlNotice.detail")
@ViewDescriptor("fund-control-notice-detail-view.xml")
@EditedEntityContainer("fundControlNoticeDc")
public class FundControlNoticeDetailView extends StandardDetailView<FundControlNotice> {
}