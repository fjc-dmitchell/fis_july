package gov.fjc.fis.view.obligationfragment;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.Renderer;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Group;
import gov.fjc.fis.entity.Obligation;
import gov.fjc.fis.service.ObligationService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@FragmentDescriptor("obligation-fragment.xml")
public class ObligationFragment extends Fragment<VerticalLayout> {
    @ViewComponent
    private CollectionLoader<Obligation> obligationsDl;
    @Autowired
    private ObligationService obligationService;
    private Object entity;

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        obligationsDl.load();
    }

    @Install(to = "obligationsDl", target = Target.DATA_LOADER)
    protected List<Obligation> obligationsDlLoadDelegate(final LoadContext<Obligation> loadContext) {
        List<Obligation> obligations = new ArrayList<>();
        switch (entity.getClass().getSimpleName()) {
            case "Group" -> obligations = obligationService.getObligations((Group) entity);
            case "Branch" -> obligations = obligationService.getObligations((Branch) entity);
        }
        return obligations;
    }
}