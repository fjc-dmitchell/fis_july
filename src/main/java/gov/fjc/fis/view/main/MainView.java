package gov.fjc.fis.view.main;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.bean.LandingPageGenerator;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.event.AppropriationClosedEvent;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.service.AppropriationService;
import io.jmix.core.LoadContext;
import io.jmix.core.Resources;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.multiselectcomboboxpicker.JmixMultiSelectComboBoxPicker;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Route("")
@ViewController("fis_MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    private UiEventPublisher uiEventPublisher;
    @ViewComponent
    private CollectionLoader<Appropriation> bfyEntryDl;
    @Autowired
    private AppropriationService appropriationService;

    @ViewComponent
    private EntityComboBox<Appropriation> bfyEntry;
    @ViewComponent
    private JmixMultiSelectComboBoxPicker<Appropriation> bfySearch;
    @Autowired
    private SessionData sessionData;

    @Subscribe
    public void onInit(final InitEvent event) {
        bfyEntry.setValue(appropriationService.getCurrentBudgetFiscalYear());
        bfySearch.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);
    }

    @Install(to = "bfyEntryDl", target = Target.DATA_LOADER)
    private List<Appropriation> bfyEntryDlLoadDelegate(final LoadContext<Appropriation> loadContext) {
        return appropriationService.getOpenAppropriations();
    }

    @Install(to = "bfySearchDl", target = Target.DATA_LOADER)
    private List<Appropriation> bfySearchDlLoadDelegate(final LoadContext<Appropriation> loadContext) {
        return appropriationService.getAppropriations();
    }

    @Subscribe("bfyEntry")
    public void onBfyEntryComponentValueChange(final AbstractField.ComponentValueChangeEvent event) {
        if (event.getValue() == null) {
            bfyEntry.setValue(appropriationService.getCurrentBudgetFiscalYear());
        }
        sessionData.setAttribute("bfyEntry", bfyEntry.getValue());
        // ToDo: check for null before the following statement to avoid exception on bfyEntry.getValue()
        uiEventPublisher.publishEvent(new FiscalYearChangeEvent(this, bfyEntry.getValue().getBudgetFiscalYear()));
    }

    @Subscribe("bfySearch")
    public void onBfySearchComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixMultiSelectComboBoxPicker<Appropriation>, Set<?>> event) {
        sessionData.setAttribute("bfySearch", bfySearch.getValue());
        uiEventPublisher.publishEvent(new FiscalYearChangeEvent(this, "searchYears"));
    }

    @Async
    @EventListener
    public void handleAsyncEvent(AppropriationClosedEvent event) {
        bfyEntryDl.load();
    }

    // Doug added everything below to create landing page
    @Subscribe
    public void onReady(final ReadyEvent event) {
        createLandingLayout();
    }

    @Autowired
    private LandingPageGenerator landingPageGenerator;

    private void createLandingLayout() {
        if (getContent().getContent() == null) {
//            getContent().setContent(overviewPageGenerator.generate(
//                    "main",
//                    "io/jmix/uisamples/view/sys/main/main-overview.xml"));
        }
    }
}
