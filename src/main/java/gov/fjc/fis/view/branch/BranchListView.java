package gov.fjc.fis.view.branch;

import gov.fjc.fis.entity.Branch;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "branches", layout = MainView.class)
@ViewController("fis_Branch.list")
@ViewDescriptor("branch-list-view.xml")
@LookupComponent("branchesDataGrid")
@DialogMode(width = "64em")
public class BranchListView extends StandardListView<Branch> {
}