package gov.fjc.fis.view.mainviewfragment;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.UserMessageService;
import gov.fjc.fis.service.report.StatusOfFundsReportService;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

@FragmentDescriptor("main-view-fragment.xml")
public class MainViewFragment extends Fragment<VerticalLayout> {
    @Autowired
    private SessionData sessionData;
    @ViewComponent
    private CollectionLoader<Appropriation> appropriationsDl;
    @ViewComponent
    private CollectionLoader<CategoryDto> categoryDtosDl;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private StatusOfFundsReportService statusOfFundsReportService;
    @ViewComponent
    private EntityComboBox<Appropriation> appropriationsComboBox;
    @ViewComponent
    private JmixDetails oneYearDetails;
    @ViewComponent
    private JmixDetails twoYearDetails;
    @ViewComponent
    private VerticalLayout messageBox;
    private Appropriation appropriation;
    @ViewComponent
    private Chart pie;

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostInit(final View.InitEvent event) {
        appropriationsDl.load();
        appropriationRefresh();
        var userMessage = userMessageService.getCurrentMessage();
        if (userMessage.getMessage() != null && !userMessage.getMessage().isEmpty()) {
            var message = "<div>".concat(userMessage.getMessage()).concat("</div>");
            messageBox.add(new Html(message));
        }
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostBeforeShow(final View.BeforeShowEvent event) {
        ListChartItems<MapDataItem> items = new ListChartItems<>(
                new MapDataItem(Map.of("category", "Completed", "value", 20)),
                new MapDataItem(Map.of("category", "Not Started", "value", 30)),
                new MapDataItem(Map.of("category", "In Progress", "value", 40))
        );

        pie.setDataSet(
                new DataSet()
                        .withSource(
                                new DataSet.Source<MapDataItem>()
                                        .withDataProvider(items)
                                        .withCategoryField("category")
                                        .withValueField("value")
                        )
        );
    }

    @Install(to = "appropriationsDl", target = Target.DATA_LOADER)
    protected List<Appropriation> appropriationsDlLoadDelegate(final LoadContext<Appropriation> loadContext) {
        return appropriationService.getAppropriations();
    }

    @Install(to = "categoryDtosDl", target = Target.DATA_LOADER)
    protected List<CategoryDto> categoryDtosDlLoadDelegate(final LoadContext<CategoryDto> loadContext) {
        return statusOfFundsReportService.getStatusOfFundsCategoryData(appropriation, 0);
    }

    @Subscribe("appropriationsComboBox")
    protected void onAppropriationsComboBoxComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Appropriation>, Appropriation> event) {
        appropriation = appropriationsComboBox.getValue();
        oneYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" One Year Appropriation"));
        twoYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" Two Year Appropriation"));
        categoryDtosDl.load();
    }

    private void appropriationRefresh() {
        Appropriation bfyEntry = appropriationService.getBfyEntryAppropriation(sessionData);
        if (bfyEntry != appropriation) {
            appropriation = bfyEntry;
            appropriationsComboBox.setValue(appropriation);
            //setTitles()
        }
    }

    @Subscribe("landingTabSheet")
    protected void onLandingTabSheetSelectedChange(final JmixTabSheet.SelectedChangeEvent event) {
        appropriationRefresh();
        if (event.getSelectedTab().equals(appropriationsComboBox)) {}
    }

    @Async
    @EventListener
    public void handleAsyncEvent(FiscalYearChangeEvent event) {
        appropriationRefresh();
    }

}