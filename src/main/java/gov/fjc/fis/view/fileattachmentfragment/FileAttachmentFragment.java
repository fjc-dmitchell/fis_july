package gov.fjc.fis.view.fileattachmentfragment;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.data.selection.SelectionEvent;
import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.FileAttachment;
import gov.fjc.fis.entity.Invoice;
import gov.fjc.fis.entity.Obligation;
import io.jmix.core.DataManager;
import io.jmix.core.EntityStates;
import io.jmix.core.FileRef;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.upload.FileStorageUploadField;
import io.jmix.flowui.component.upload.receiver.FileTemporaryStorageBuffer;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.upload.event.FileUploadSucceededEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.upload.TemporaryStorage;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.Target;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@FragmentDescriptor("file-attachment-fragment.xml")
public class FileAttachmentFragment extends Fragment<VerticalLayout> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private TemporaryStorage temporaryStorage;
    @Autowired
    private Downloader downloader;

    @ViewComponent
    private CollectionContainer<FileAttachment> attachmentsDc;
    @ViewComponent
    private DataGrid<FileAttachment> attachmentsDataGrid;
    @ViewComponent
    private FileStorageUploadField fileAttachField;
    @ViewComponent
    private JmixButton downloadButton;
    @ViewComponent
    private Paragraph unsavedMessage;
    @ViewComponent
    private VerticalLayout attachmentsBox;
    private Object hostEntity;

    private Activity activity;
    private Obligation obligation;
    private Invoice invoice;

    public void setHostEntity(Object hostEntity) {
        this.hostEntity = hostEntity;

        if (hostEntity.getClass().equals(Activity.class)) {
            activity = (Activity) hostEntity;
        } else if (hostEntity.getClass().equals(Obligation.class)) {
            obligation = (Obligation) hostEntity;
            activity = obligation.getActivity();
        } else if (hostEntity.getClass().equals(Invoice.class)) {
            invoice = (Invoice) hostEntity;
            obligation = invoice.getObligation();
            activity = obligation.getActivity();
        }
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostAttach(final AttachEvent event) {
        if (hostEntity == null) {
            throw new IllegalStateException("hostEntity is null in FileAttachmentFragment");
        }
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostBeforeShow(final View.BeforeShowEvent event) {
        if (entityStates.isNew(hostEntity)) {
            unsavedMessage.setVisible(true);
            attachmentsBox.setVisible(false);
        }
        attachmentsDataGrid.addComponentColumn(attachment -> {
            Button button = uiComponents.create(Button.class);
            button.setText("Download");
            button.setIcon(VaadinIcon.DOWNLOAD_ALT.create());
            button.addThemeName("tertiary-inline");
            button.addClickListener(clickEvent -> downloadFromFileStorage(attachment));
            return button;
        });
        attachmentsDataGrid.addComponentColumn(attachment -> {
            Button button = uiComponents.create(Button.class);
            button.setText("View");
            button.setIcon(VaadinIcon.ARROW_FORWARD.create());
            button.addThemeName("tertiary-inline");
            button.addClickListener(clickEvent -> viewFromFileStorage(attachment));
            return button;
        });
    }

    @Subscribe("attachmentsDataGrid")
    protected void onAttachmentsDataGridSelection(final SelectionEvent<DataGrid<FileAttachment>, FileAttachment> event) {
        var selectedItems = attachmentsDataGrid.getSelectedItems();
        downloadButton.setEnabled(!selectedItems.isEmpty());
        downloadButton.setText("Download (".concat(String.valueOf(selectedItems.size())).concat(")"));
    }

    @Subscribe("fileAttachField")
    protected void onFileAttachFieldFileUploadSucceeded(final FileUploadSucceededEvent<FileStorageUploadField> event) {
        Receiver receiver = event.getReceiver();
        if (receiver instanceof FileTemporaryStorageBuffer) {
            assert ((FileTemporaryStorageBuffer) receiver)
                    .getFileData() != null;
            UUID fileId = ((FileTemporaryStorageBuffer) receiver)
                    .getFileData().getFileInfo().getId();

            Long contentLength = event.getContentLength();
            FileRef fileRef = temporaryStorage.putFileIntoStorage(fileId, event.getFileName());

            // create attachment
            var attachment = dataManager.create(FileAttachment.class);
            attachment.setFileReference(fileRef);
            attachment.setContentLength(contentLength);
            attachment.setActivity(activity);
            attachment.setObligation(obligation);
            attachment.setInvoice(invoice);
            dataManager.save(attachment); // otherwise, we could end up with orphaned files
            attachmentsDc.getMutableItems().add(attachment);

            fileAttachField.clear();

        }
    }

    @Subscribe(id = "downloadButton", subject = "clickListener")
    protected void onDownloadButtonClick(final ClickEvent<JmixButton> event) {
        attachmentsDataGrid.getSelectedItems().forEach(this::downloadFromFileStorage);
    }

    private void downloadFromFileStorage(FileAttachment attachment) {
        FileRef fileRef = attachment.getFileReference();
        downloader.setShowNewWindow(false);
        downloader.download(fileRef);
    }

    private void viewFromFileStorage(FileAttachment attachment) {
        FileRef fileRef = attachment.getFileReference();
        downloader.setShowNewWindow(true);
        downloader.download(fileRef);
    }
}