package gov.fjc.fis.view.ape;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.form.Ape;

import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.service.ApeService;
import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.BranchService;
import gov.fjc.fis.service.DivisionService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "apes", layout = MainView.class)
@ViewController("fis_Ape.list")
@ViewDescriptor("ape-list-view.xml")
@LookupComponent("apesDataGrid")
@DialogMode(width = "64em")
public class ApeListView extends StandardListView<Ape> {
    @Autowired
    private SessionData sessionData;
    @ViewComponent
    private CollectionLoader<Ape> apesDl;
    @ViewComponent
    private CollectionLoader<Branch> branchesDl;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private ApeService apeService;
    @Autowired
    private BranchService branchService;

    @ViewComponent
    private JmixButton showBfyBtn;
    @ViewComponent
    private EntityComboBox<Branch> branchSearchField;
    @ViewComponent
    private TypedTextField<Object> programContactSearch;

    private List<Appropriation> fiscalYears;
    private String divisionCode;

    @Subscribe
    protected void onInit(final InitEvent event) {
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        branchesDl.load();
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
//        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        setBfyBtnCaption();
    }

    private void setBfyBtnCaption() {
        if (fiscalYears.size() == 1) {
            showBfyBtn.setText("Show all for " + fiscalYears.get(0).getBudgetFiscalYear());
        } else {
            showBfyBtn.setText("Show Search BFYs");
        }
    }

    @Install(to = "apesDl", target = Target.DATA_LOADER)
    protected List<Ape> apesDlLoadDelegate(final LoadContext<Ape> loadContext) {
        return apeService.getApes(fiscalYears,branchSearchField.getValue(), programContactSearch.getValue());
    }

    @Install(to = "branchesDl", target = Target.DATA_LOADER)
    protected List<Branch> branchesDlLoadDelegate(final LoadContext<Branch> loadContext) {
        return branchService.getBranchSearchList(fiscalYears, "2");
    }

    @Install(to = "branchSearchField", subject = "itemLabelGenerator")
    protected Object branchSearchFieldItemLabelGenerator(final Branch branch) {
        return branch.getTitleAndCode();
    }

    @Subscribe("branchSearchField")
    protected void onBranchSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Branch>, Branch> event) {
       apesDl.load();
    }

    @Subscribe("programContactSearch")
    protected void onProgramContactSearchComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedTextField<?>, ?> event) {
        apesDl.load();
    }

    @Subscribe(id = "showBfyBtn", subject = "clickListener")
    protected void onShowBfyBtnClick(final ClickEvent<JmixButton> event) {
        branchSearchField.clear();
        programContactSearch.clear();
        apesDl.load();
    }

    /**
     * Changes the Show BFY button caption after a Fiscal Year change event
     *
     * @param event
     */
    @Async
    @EventListener
    public void handleAsyncEvent(FiscalYearChangeEvent event) {
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        setBfyBtnCaption();
    }
}