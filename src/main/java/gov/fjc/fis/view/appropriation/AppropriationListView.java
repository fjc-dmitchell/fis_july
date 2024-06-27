package gov.fjc.fis.view.appropriation;

import gov.fjc.fis.entity.Appropriation;

import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "appropriations", layout = MainView.class)
@ViewController("fis_Appropriation.list")
@ViewDescriptor("appropriation-list-view.xml")
@LookupComponent("appropriationsDataGrid")
@DialogMode(width = "64em")
public class AppropriationListView extends StandardListView<Appropriation> {
    @Autowired
    private AppropriationService appropriationService;

    @Install(to = "appropriationsDl", target = Target.DATA_LOADER)
    private List<Appropriation> appropriationsDlLoadDelegate(final LoadContext<Appropriation> loadContext) {
        return appropriationService.getAppropriations();
    }

}