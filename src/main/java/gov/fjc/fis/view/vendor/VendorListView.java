package gov.fjc.fis.view.vendor;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Vendor;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;


@Route(value = "vendors", layout = MainView.class)
@ViewController(id = "fis_Vendor.list")
@ViewDescriptor(path = "vendor-list-view.xml")
@LookupComponent("vendorsDataGrid")
@DialogMode(width = "64em")
public class VendorListView extends StandardListView<Vendor> {
    @Autowired
    private UiComponents uiComponents;

    @Supply(to = "vendorsDataGrid.active", subject = "renderer")
    protected Renderer<Vendor> vendorsDataGridActiveRenderer() {
        return new ComponentRenderer<>(vendor -> {
            Checkbox checkbox = uiComponents.create(Checkbox.class);
            checkbox.setValue(vendor.getActive());
            checkbox.setReadOnly(true);
            return checkbox;
        });
    }

}