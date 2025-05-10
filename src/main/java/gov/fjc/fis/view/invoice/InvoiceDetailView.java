package gov.fjc.fis.view.invoice;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.Invoice;
import gov.fjc.fis.view.fileattachmentfragment.FileAttachmentFragment;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;

@Route(value = "invoices/:id", layout = MainView.class)
@ViewController(id = "fis_Invoice.detail")
@ViewDescriptor(path = "invoice-detail-view.xml")
@EditedEntityContainer("invoiceDc")
public class InvoiceDetailView extends StandardDetailView<Invoice> {
    @ViewComponent
    private FileAttachmentFragment attachmentFragment;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        attachmentFragment.setHostEntity(getEditedEntity());
    }
}