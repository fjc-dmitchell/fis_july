package gov.fjc.fis.view.fileattachment;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.FileAttachment;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "file-attachments/:id", layout = MainView.class)
@ViewController(id = "fis_FileAttachment.detail")
@ViewDescriptor(path = "file-attachment-detail-view.xml")
@EditedEntityContainer("fileAttachmentDc")
public class FileAttachmentDetailView extends StandardDetailView<FileAttachment> {
}