package gov.fjc.fis.view.group;

import gov.fjc.fis.entity.Group;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "groups", layout = MainView.class)
@ViewController("fis_Group.list")
@ViewDescriptor("group-list-view.xml")
@LookupComponent("groupsDataGrid")
@DialogMode(width = "64em")
public class GroupListView extends StandardListView<Group> {
}