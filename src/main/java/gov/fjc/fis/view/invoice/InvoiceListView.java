package gov.fjc.fis.view.invoice;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Invoice;
import gov.fjc.fis.view.main.MainView;
import gov.fjc.fis.view.search.CustomSearchFragment;
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
    private CustomSearchFragment searchFragment;

    private boolean fjcFoundation = false;

    protected void setFjcFoundation() {
        this.fjcFoundation = true;
    }

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        searchFragment.setFjcFoundation(fjcFoundation);
    }
}