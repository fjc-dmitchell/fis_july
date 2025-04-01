package gov.fjc.fis.view.donor;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Donor;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "donors/:id", layout = MainView.class)
@ViewController("fis_Donor.detail")
@ViewDescriptor("donor-detail-view.xml")
@EditedEntityContainer("donorDc")
public class DonorDetailView extends StandardDetailView<Donor> {
}