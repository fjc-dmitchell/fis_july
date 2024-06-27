package gov.fjc.fis.view.branch;

import gov.fjc.fis.entity.Branch;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "branches/:id", layout = MainView.class)
@ViewController("fis_Branch.detail")
@ViewDescriptor("branch-detail-view.xml")
@EditedEntityContainer("branchDc")
public class BranchDetailView extends StandardDetailView<Branch> {
}