package gov.fjc.fis.view.documentexception;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.DocumentException;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "document-exceptions", layout = MainView.class)
@ViewController(id = "fis_DocumentException.list")
@ViewDescriptor(path = "document-exception-list-view.xml")
@LookupComponent("documentExceptionsDataGrid")
@DialogMode(width = "64em")
public class DocumentExceptionListView extends StandardListView<DocumentException> {
}