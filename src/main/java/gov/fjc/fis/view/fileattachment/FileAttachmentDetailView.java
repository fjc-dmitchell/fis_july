package gov.fjc.fis.view.fileattachment;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.FileAttachment;
import gov.fjc.fis.view.main.MainView;
import io.jmix.core.EntityStates;
import io.jmix.core.FileRef;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "file-attachments/:id", layout = MainView.class)
@ViewController(id = "fis_FileAttachment.detail")
@ViewDescriptor(path = "file-attachment-detail-view.xml")
@EditedEntityContainer("fileAttachmentDc")
public class FileAttachmentDetailView extends StandardDetailView<FileAttachment> {
}