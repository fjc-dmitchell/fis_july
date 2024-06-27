package gov.fjc.fis.view.invoice;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Invoice;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.DataGridColumnVisibilityChangedEvent;
import io.jmix.flowui.view.*;

@Route(value = "invoices", layout = MainView.class)
@ViewController("fis_Invoice.list")
@ViewDescriptor("invoice-list-view.xml")
@LookupComponent("invoicesDataGrid")
@DialogMode(width = "64em")
public class InvoiceListView extends StandardListView<Invoice> {
    @ViewComponent
    private DataGrid<Invoice> invoicesDataGrid;

    @Subscribe
    public void onInit(final InitEvent event) {
//        invoicesDataGrid.setWidth("1440px");
    }

    @Subscribe("invoicesDataGrid.[obligation.activity.branch.title]")
    public void onInvoicesDataGridObligationActivityBranchTitleDataGridColumnVisibilityChanged(final DataGridColumnVisibilityChangedEvent<Invoice> event) {
//        invoicesDataGrid.recalculateColumnWidths();
    }
}