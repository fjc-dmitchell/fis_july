package gov.fjc.fis.view.documentaudit;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.DocumentAudit;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "document-audits/:id", layout = MainView.class)
@ViewController(id = "fis_DocumentAudit.detail")
@ViewDescriptor(path = "document-audit-detail-view.xml")
@EditedEntityContainer("documentAuditDc")
public class DocumentAuditDetailView extends StandardDetailView<DocumentAudit> {
}