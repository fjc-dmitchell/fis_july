package gov.fjc.fis.view.report.opentravelobligationsreport;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;

@Route(value = "open-travel-obligations-report-view", layout = DefaultMainViewParent.class)
@ViewController(id = "fis_OpenTravelObligationsReportView")
@ViewDescriptor(path = "open-travel-obligations-report-view.xml")
public class OpenTravelObligationsReportView extends StandardView {



    @Subscribe(id = "cancelBtn", subject = "clickListener")
    protected void onCancelBtnClick(final ClickEvent<JmixButton> event) {
        closeWithDefaultAction();
    }

    @Subscribe(id = "executeBtn", subject = "clickListener")
    protected void onExecuteBtnClick(final ClickEvent<JmixButton> event) {
        closeWithDefaultAction();
    }
}