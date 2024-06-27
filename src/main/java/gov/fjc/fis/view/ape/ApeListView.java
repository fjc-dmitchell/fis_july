package gov.fjc.fis.view.ape;

import gov.fjc.fis.entity.form.Ape;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "apes", layout = MainView.class)
@ViewController("fis_Ape.list")
@ViewDescriptor("ape-list-view.xml")
@LookupComponent("apesDataGrid")
@DialogMode(width = "64em")
public class ApeListView extends StandardListView<Ape> {
}