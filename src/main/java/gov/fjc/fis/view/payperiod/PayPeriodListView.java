package gov.fjc.fis.view.payperiod;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.PayPeriod;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "payPeriods", layout = MainView.class)
@ViewController("fis_PayPeriod.list")
@ViewDescriptor("pay-period-list-view.xml")
@LookupComponent("payPeriodsDataGrid")
@DialogMode(width = "64em")
public class PayPeriodListView extends StandardListView<PayPeriod> {
}