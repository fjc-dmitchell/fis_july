package gov.fjc.fis.view.jitftransfer;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.JitfTransfer;
import io.jmix.flowui.view.*;


@Route(value = "jitf-transfers", layout = DefaultMainViewParent.class)
@ViewController(id = "fis_JitfTransfer.list")
@ViewDescriptor(path = "jitf-transfer-list-view.xml")
@LookupComponent("jitfTransfersDataGrid")
@DialogMode(width = "64em")
public class JitfTransferListView extends StandardListView<JitfTransfer> {
}