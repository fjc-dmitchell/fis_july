package gov.fjc.fis.view.group;

import gov.fjc.fis.entity.Group;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "groups/:id", layout = MainView.class)
@ViewController("fis_Group.detail")
@ViewDescriptor("group-detail-view.xml")
@EditedEntityContainer("groupDc")
public class GroupDetailView extends StandardDetailView<Group> {
}