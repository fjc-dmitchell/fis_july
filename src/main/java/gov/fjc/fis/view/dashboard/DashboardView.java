package gov.fjc.fis.view.dashboard;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.AppropriationType;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.service.report.StatusOfFundsReportService;
import gov.fjc.fis.view.main.MainView;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import io.jmix.core.LoadContext;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Route(value = "dashboard-view", layout = MainView.class)
@ViewController("fis_DashboardView")
@ViewDescriptor("dashboard-view.xml")
public class DashboardView extends StandardView {
    @Autowired
    private SessionData sessionData;
    @ViewComponent
    private CollectionLoader<Appropriation> appropriationsDl;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private StatusOfFundsReportService statusOfFundsReportService;
    @ViewComponent
    private CollectionLoader<CategoryDto> categoryDtosDl;

    @ViewComponent
    private JmixDetails oneYearDetails;
    @ViewComponent
    private JmixDetails twoYearDetails;
    @ViewComponent
    private EntityComboBox<Appropriation> appropriationsComboBox;
    @ViewComponent
    private JmixComboBox<AppropriationType> appropriationType;
    Appropriation appropriation;

    @ViewComponent
    private Chart allocationsChart;
    @ViewComponent
    private Chart spendingChart;

    @Subscribe
    protected void onInit(final InitEvent event) {
        appropriationType.setValue(AppropriationType.COMBINED_YEAR_FUND);
        appropriationsDl.load();
        appropriationRefresh();
    }

    private void onStateChange() {
        // get non-zero allocations matching year requirement
        // get non-zero spending matching year requirement
        if (appropriationType.getValue() != null) {
            ListChartItems<MapDataItem> allocations = new ListChartItems<>();
            ListChartItems<MapDataItem> spending = new ListChartItems<>();
            for (var cat : categoryDtosDc.getItems()) {
                switch (appropriationType.getValue()) {
                    case ONE_YEAR_FUND:
                        if (cat.getTotalOneYearAllocations().signum() != 0) {
                            allocations.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalOneYearAllocations())));
                        }
                        allocationsChart.getTitle().setText("One Year Allocations");
                        if (cat.getTotalOneYearObligations().signum() != 0) {
                            spending.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalOneYearObligations())));
                        }
                        spendingChart.getTitle().setText("One Year Spending");
                        break;
                    case TWO_YEAR_FUND:
                        if (cat.getTotalTwoYearAllocations().signum() != 0) {
                            allocations.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalTwoYearAllocations())));
                        }
                        allocationsChart.getTitle().setText("Two Year Allocations");
                        if (cat.getTotalTwoYearObligations().signum() != 0) {
                            spending.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalTwoYearObligations())));
                        }
                        spendingChart.getTitle().setText("Two Year Spending");
                        break;
                    default:
                        if (cat.getTotalAllocations().signum() != 0) {
                            allocations.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalAllocations())));
                        }
                        allocationsChart.getTitle().setText("Combined Year Allocations");
                        if (cat.getTotalObligations().signum() != 0) {
                            spending.addItem(new MapDataItem(Map.of("category", cat.getTitleAndCode(), "value", cat.getTotalObligations())));
                        }
                        spendingChart.getTitle().setText("Combined Year Spending");
                }
            }
            allocationsChart.setDataSet(new DataSet().withSource(new DataSet.Source<MapDataItem>().withDataProvider(allocations).withCategoryField("category").withValueField("value")));
            spendingChart.setDataSet(new DataSet().withSource(new DataSet.Source<MapDataItem>().withDataProvider(spending).withCategoryField("category").withValueField("value")));
        }
    }


    @Subscribe
    protected void onReady(final ReadyEvent event) {
        appropriationType.setValue(AppropriationType.COMBINED_YEAR_FUND);
    }

    @Install(to = "appropriationsDl", target = Target.DATA_LOADER)
    protected List<Appropriation> appropriationsDlLoadDelegate(final LoadContext<Appropriation> loadContext) {
        return appropriationService.getAppropriations();
    }

    @Install(to = "categoryDtosDl", target = Target.DATA_LOADER)
    protected List<CategoryDto> categoryDtosDlLoadDelegate(final LoadContext<CategoryDto> loadContext) {
        return statusOfFundsReportService.getStatusOfFundsCategoryDataWithUnused(appropriation, 0);
    }

    @Subscribe("appropriationsComboBox")
    protected void onAppropriationsComboBoxComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Appropriation>, Appropriation> event) {
        appropriation = appropriationsComboBox.getValue();
        oneYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" One Year Appropriation"));
        twoYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" Two Year Appropriation"));
        allocationsChart.getTitle().setSubtext("Budget Fiscal Year ".concat(appropriation.getBudgetFiscalYear()));
        spendingChart.getTitle().setSubtext("Budget Fiscal Year ".concat(appropriation.getBudgetFiscalYear()));
        categoryDtosDl.load();
        onStateChange();
    }

    @ViewComponent
    private CollectionContainer<CategoryDto> categoryDtosDc;

    @Subscribe("appropriationType")
    protected void onAppropriationTypeComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<AppropriationType>, AppropriationType> event) {
        onStateChange();
    }

    private void appropriationRefresh() {
        Appropriation bfyEntry = appropriationService.getBfyEntryAppropriation(sessionData);
        if (bfyEntry != appropriation) {
            appropriation = bfyEntry;
            appropriationsComboBox.setValue(appropriation);
            //setTitles()
        }
    }

    @Async
    @EventListener
    public void handleAsyncEvent(FiscalYearChangeEvent event) {
        appropriationRefresh();
    }
}