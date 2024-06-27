package gov.fjc.fis.view.jitf;

import gov.fjc.fis.entity.Jitf;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "jitfs", layout = MainView.class)
@ViewController("fis_Jitf.list")
@ViewDescriptor("jitf-list-view.xml")
@LookupComponent("jitfsDataGrid")
@DialogMode(width = "64em")
public class JitfListView extends StandardListView<Jitf> {
}