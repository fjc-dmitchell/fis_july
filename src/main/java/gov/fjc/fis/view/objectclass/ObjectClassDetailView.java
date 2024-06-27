package gov.fjc.fis.view.objectclass;

import gov.fjc.fis.entity.ObjectClass;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "objectClasses/:id", layout = MainView.class)
@ViewController("fis_ObjectClass.detail")
@ViewDescriptor("object-class-detail-view.xml")
@EditedEntityContainer("objectClassDc")
public class ObjectClassDetailView extends StandardDetailView<ObjectClass> {
}