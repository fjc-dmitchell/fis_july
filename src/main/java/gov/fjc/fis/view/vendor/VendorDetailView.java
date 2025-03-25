package gov.fjc.fis.view.vendor;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Vendor;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "vendors/:id", layout = MainView.class)
@ViewController(id = "fis_Vendor.detail")
@ViewDescriptor(path = "vendor-detail-view.xml")
@EditedEntityContainer("vendorDc")
public class VendorDetailView extends StandardDetailView<Vendor> {
    @Autowired
    private EntityStates entityStates;
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
       Vendor vendor = getEditedEntity();
       if(!entityStates.isNew(vendor)) {
           createdByString.setText(vendor.getCreatedByString());
       }
    }
}