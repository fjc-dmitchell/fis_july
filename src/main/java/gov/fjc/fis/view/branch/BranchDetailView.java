package gov.fjc.fis.view.branch;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Branch;

import gov.fjc.fis.view.activityfragment.ActivityFragment;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.obligationfragment.ObligationFragment;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "branches/:id", layout = MainView.class)
@ViewController("fis_Branch.detail")
@ViewDescriptor("branch-detail-view.xml")
@EditedEntityContainer("branchDc")
public class BranchDetailView extends StandardDetailView<Branch> {
    @Autowired
    private Fragments fragments;
    @ViewComponent
    private Paragraph createdByString;
    @ViewComponent
    private HorizontalLayout activitiesFragment;
    @ViewComponent
    private HorizontalLayout obligationsFragment;

    @Subscribe
    protected void onBeforeShow(final BeforeShowEvent event) {
        var group = getEditedEntity();
        createdByString.setText(group.getCreatedByString());

        ActivityFragment fragment = fragments.create(this, ActivityFragment.class);
        fragment.setEntity(group);
        activitiesFragment.add(fragment);

        ObligationFragment obligationFragment= fragments.create(this, ObligationFragment.class);
        obligationFragment.setEntity(group);

        obligationsFragment.add(obligationFragment);
    }
}