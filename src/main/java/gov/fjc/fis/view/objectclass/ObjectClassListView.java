package gov.fjc.fis.view.objectclass;

import gov.fjc.fis.entity.ObjectClass;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "objectClasses", layout = MainView.class)
@ViewController("fis_ObjectClass.list")
@ViewDescriptor("object-class-list-view.xml")
@LookupComponent("objectClassesDataGrid")
@DialogMode(width = "64em")
public class ObjectClassListView extends StandardListView<ObjectClass> {
}