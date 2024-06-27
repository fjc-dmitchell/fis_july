package gov.fjc.fis.view.appropriation;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;

import gov.fjc.fis.entity.AppropriationAdjustment;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.dto.AmountsDto;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.event.AppropriationClosedEvent;
import gov.fjc.fis.service.AppropriationAdjustmentService;
import gov.fjc.fis.service.DivisionService;
import gov.fjc.fis.service.report.StatusOfFundsReportService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route(value = "appropriations/:id", layout = MainView.class)
@ViewController("fis_Appropriation.detail")
@ViewDescriptor("appropriation-detail-view.xml")
@EditedEntityContainer("appropriationDc")
public class AppropriationDetailView extends StandardDetailView<Appropriation> {
    @Autowired
    private DivisionService divisionService;
    @ViewComponent
    private Paragraph createdByString;
    @ViewComponent
    private JmixButton adjustEdit;
    @ViewComponent
    private JmixButton adjustDelete;
    @ViewComponent
    private DataGrid<AppropriationAdjustment> adjustmentsDataGrid;
    @ViewComponent
    private VerticalLayout testing;
    @Autowired
    private AppropriationAdjustmentService appropriationAdjustmentService;
    @ViewComponent
    private DataGrid<AmountsDto> amountsDtoesDataGrid;
    @ViewComponent
    private JmixCheckbox statusField;
    @ViewComponent
    private TypedTextField<BigDecimal> oneYearAmountField;
    @ViewComponent
    private TypedTextField<Object> twoYearAmountField;
    @Autowired
    private UiEventPublisher flowui_UiEventPublisher;
    @ViewComponent
    private TypedTextField<String> budgetFiscalYearField;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private StatusOfFundsReportService statusOfFundsReportService;
    @ViewComponent
    private CollectionContainer<CategoryDto> categoryDtoDc;
    @ViewComponent
    private CollectionLoader<CategoryDto> categoryDtoDl;
    private Appropriation appropriation;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        appropriation = getEditedEntity();
        setEditable(appropriation.getStatus());

        if(entityStates.isNew(appropriation)) {

        } else {
            categoryDtoDl.load();
        }

        budgetFiscalYearField.setReadOnly(!entityStates.isNew(appropriation));
        createdByString.setText(getEditedEntity().getCreatedByString());

        amountsDtoesDataGrid.setItems(appropriationAdjustmentService.getAdjustments(getEditedEntity()));

//        Grid<AmountsDto> grid = new Grid<>(AmountsDto.class, false);
//        grid.addColumn(AmountsDto::getTitle).setHeader("name");
//        grid.addColumn(AmountsDto::getOneYearAmount).setHeader("One Year").setWidth("10em");
//        grid.addColumn(AmountsDto::getTwoYearAmount).setHeader("Two Year").setWidth("10em");
//        grid.setItems(appropriationAdjustmentService.getAdjustments(getEditedEntity()));
//
//        testing.add(grid);

    }

    @Subscribe("statusField")
    public void onStatusFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixCheckbox, ?> event) {
        setEditable(statusField.getValue());
    }

    @Install(to = "divisionsDl", target = Target.DATA_LOADER)
    private List<Division> divisionsDlLoadDelegate(final LoadContext<Division> loadContext) {
        return divisionService.getDivisions(getEditedEntity(), false);
    }

    @Install(to = "categoryDtoDl", target = Target.DATA_LOADER)
    private List<CategoryDto> categoryDtoDlLoadDelegate(final LoadContext<CategoryDto> loadContext) {
        return statusOfFundsReportService.getStatusOfFundsCategoryData(appropriation, 2);
    }



    @Subscribe(id = "adjustmentsDc", target = Target.DATA_CONTAINER)
    public void onAdjustmentsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<AppropriationAdjustment> event) {
        var empty = getEditedEntity().getAdjustments().isEmpty();
        adjustEdit.setVisible(!empty);
        adjustDelete.setVisible(!empty);
        adjustmentsDataGrid.setVisible(!empty);
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    public void onPostSave(final DataContext.PostSaveEvent event) {
       flowui_UiEventPublisher.publishEvent(new AppropriationClosedEvent(this,"appropriationClosed"));
    }

    private void setEditable(boolean status) {
       oneYearAmountField.setReadOnly(!status);
       twoYearAmountField.setReadOnly(!status);
//       adjustmentsDataGrid.setEnabled(status);
       // each button for grid?

    }
}