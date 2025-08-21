package gov.fjc.fis.view.main;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import gov.fjc.fis.bean.LandingPageGenerator;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.UserMessage;
import gov.fjc.fis.event.AppropriationClosedEvent;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.event.NewAppropriationEvent;
import gov.fjc.fis.event.UserMessageSavedEvent;
import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.UserMessageService;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.multiselectcomboboxpicker.JmixMultiSelectComboBoxPicker;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.tabbedmode.app.main.StandardTabbedModeMainView;
import io.jmix.tabbedmode.component.tabsheet.MainTabSheet;
import io.jmix.tabbedmode.component.workarea.TabbedViewsContainer;
import io.jmix.tabbedmode.component.workarea.WorkArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Set;

@Route("")
@ViewController("fis_MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardTabbedModeMainView {

    @Autowired
    private UiEventPublisher uiEventPublisher;
    @ViewComponent
    private CollectionContainer<Appropriation> bfyEntryDc;
    @ViewComponent

    private CollectionLoader<Appropriation> bfyEntryDl;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private UserMessageService userMessageService;
    @ViewComponent
    private EntityComboBox<Appropriation> bfyEntry;
    @ViewComponent
    private CollectionLoader<Appropriation> bfySearchDl;
    @ViewComponent
    private JmixMultiSelectComboBoxPicker<Appropriation> bfySearch;
    @Autowired
    private SessionData sessionData;
    @Autowired
    private UiComponents uiComponents;
    @ViewComponent
    private Div applicationTitlePlaceholder;
    @ViewComponent
    private MessageBundle messageBundle;
    @ViewComponent
    private Header header;
    @ViewComponent
    private VerticalLayout messageBox;

    private List<UserMessage> userMessageList;

    @Subscribe
    public void onInit(final InitEvent event) {
        bfySearch.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);
        initApplicationTitle();
        userMessageList = userMessageService.getUserMessages();
        refreshMessageBox();
//        ThemeToggle themeToggle = new ThemeToggle();
//        themeToggle.setClassName("theme-toggle");
//        header.add(themeToggle);
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        // set variables in user session only if they have not been previously set in another browser tab

        var sessionBfyLimit = sessionData.getAttribute("bfyLimitYear");
        if (sessionBfyLimit == null) {
            sessionData.setAttribute("bfyLimitYear", appropriationService.getLimitBfy());
        }

        var sessionEntryBfy = (Appropriation) sessionData.getAttribute("bfyEntry");
        if (sessionEntryBfy == null) {
            bfyEntry.setValue(appropriationService.getCurrentOrLatestOpenBudgetFiscalYear());
        } else {
            bfyEntry.setValue(sessionEntryBfy);
        }

        var sessionBfySearch = (Set<Appropriation>) sessionData.getAttribute("bfySearch");
        if (sessionBfySearch != null) {
            bfySearch.setValue(sessionBfySearch);
        }
    }

    protected void initApplicationTitle() {
        RouterLink link = uiComponents.create(RouterLink.class);
        link.setRoute(MainView.class);
        link.addClassNames("jmix-main-view-header-link");

        link.add(createApplicationImage(), createApplicationText());

        applicationTitlePlaceholder.addComponentAsFirst(link);
    }

    protected Component createApplicationImage() {
        Image image = uiComponents.create(Image.class);
        image.setSrc("icons/icon.png");

        image.setWidth("3em");
        image.setHeight("3em");
        return image;
    }

    protected Component createApplicationText() {
        H2 h2 = uiComponents.create(H2.class);
        h2.setText(messageBundle.getMessage("applicationTitle.text"));
        h2.setClassName("jmix-main-view-title");
        return h2;
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
    protected void onBfyEntryComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Appropriation>, Appropriation> event) {
        var newBfyEntry = event.getValue();
        var sessionBfyEntry = (Appropriation) sessionData.getAttribute("bfyEntry");

        if (newBfyEntry == null) {
            if (sessionBfyEntry == null) {
                bfyEntry.setValue(appropriationService.getCurrentOrLatestOpenBudgetFiscalYear());
                sessionData.setAttribute("bfyEntry", bfyEntry.getValue());
                uiEventPublisher.publishEvent(new FiscalYearChangeEvent(this, bfyEntry.getValue().getBudgetFiscalYear()));
            } else {
                bfyEntry.setValue(sessionBfyEntry);
            }
        } else {
            sessionData.setAttribute("bfyEntry", newBfyEntry);
            uiEventPublisher.publishEvent(new FiscalYearChangeEvent(this, bfyEntry.getValue().getBudgetFiscalYear()));
        }
    }

    @Subscribe("bfySearch")
    public void onBfySearchComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixMultiSelectComboBoxPicker<Appropriation>, Set<?>> event) {
        var sessionBfySearch = (Set<Appropriation>) sessionData.getAttribute("bfySearch");
        if (!bfySearch.getValue().equals(sessionBfySearch)) {
            sessionData.setAttribute("bfySearch", bfySearch.getValue());
            uiEventPublisher.publishEvent(new FiscalYearChangeEvent(this, "searchYears"));
        }
    }

    // added 11/12/2024 to keep user's multiple browser tabs in sync
    @Async
    @EventListener
    public void handleAsyncEvent(FiscalYearChangeEvent event) {
        var sessionEntryBfy = (Appropriation) sessionData.getAttribute("bfyEntry");
        if (!bfyEntry.getValue().equals(sessionEntryBfy)) {
            bfyEntry.setValue(sessionEntryBfy);
        }
        var sessionBfySearch = (Set<Appropriation>) sessionData.getAttribute("bfySearch");
        if (!bfySearch.getValue().equals(sessionBfySearch)) {
            bfySearch.setValue(sessionBfySearch);
        }
    }

    @Async
    @EventListener
    public void handleAsyncEvent(AppropriationClosedEvent event) {
        bfyEntryDl.load();
        if (!bfyEntryDc.containsItem(bfyEntry.getValue())) {
            bfyEntry.setValue(appropriationService.getCurrentOrLatestOpenBudgetFiscalYear());
        }
    }

    @EventListener
    public void handleNewAppropriationEvent(NewAppropriationEvent event) {
        bfyEntryDl.load();
        bfySearchDl.load();
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

    @Subscribe("mainTabSheet")
    protected void onMainTabSheetTabsCollectionChange(final TabbedViewsContainer.TabsCollectionChangeEvent<MainTabSheet> event) {
        refreshMessageBox();
    }

    @EventListener
    public void handleUserMessageSavedEvent(UserMessageSavedEvent event) {
        userMessageList = userMessageService.getUserMessages();
        refreshMessageBox();
    }

    private void refreshMessageBox() {
        messageBox.removeAll();
        StringBuilder message = new StringBuilder("<div>");
        for (var userMessage : userMessageList) {
            message.append(userMessage.getMessage());
            message.append("<br><hr>");
        }
        message.append("</div>");
        messageBox.add(new Html(message.toString()));
    }
}
