package gov.fjc.fis.view.fileattachment;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.FileAttachment;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "file-attachments", layout = MainView.class)
@ViewController(id = "fis_FileAttachment.list")
@ViewDescriptor(path = "file-attachment-list-view.xml")
@LookupComponent("fileAttachmentsDataGrid")
@DialogMode(width = "64em")
public class FileAttachmentListView extends StandardListView<FileAttachment> {
}