package gov.fjc.fis.view.vendor;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Vendor;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "vendors", layout = MainView.class)
@ViewController(id = "fis_Vendor.list")
@ViewDescriptor(path = "vendor-list-view.xml")
@LookupComponent("vendorsDataGrid")
@DialogMode(width = "64em")
public class VendorListView extends StandardListView<Vendor> {
}