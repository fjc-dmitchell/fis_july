package gov.fjc.fis.view.jitfdashboard;


import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.dto.JitfDto;
import gov.fjc.fis.service.JitfService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "jitf-dashboard", layout = DefaultMainViewParent.class)
@ViewController(id = "fis_JitfDashboardView")
@ViewDescriptor(path = "jitf-dashboard-view.xml")
public class JitfDashboardView extends StandardView {
    @Autowired
    private JitfService jitfService;
    @ViewComponent
    private CollectionLoader<JitfDto> jitfDtoesDl;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        jitfDtoesDl.load();
    }

    @Install(to = "jitfDtoesDl", target = Target.DATA_LOADER)
    protected List<JitfDto> jitfDtoesDlLoadDelegate(final LoadContext<JitfDto> loadContext) {
        return jitfService.generateReport();
    }
}