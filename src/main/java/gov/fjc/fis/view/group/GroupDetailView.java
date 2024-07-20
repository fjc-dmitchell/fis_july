package gov.fjc.fis.view.group;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Group;

import gov.fjc.fis.view.activityfragment.ActivityFragment;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.obligationfragment.ObligationFragment;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "groups/:id", layout = MainView.class)
@ViewController("fis_Group.detail")
@ViewDescriptor("group-detail-view.xml")
@EditedEntityContainer("groupDc")
public class GroupDetailView extends StandardDetailView<Group> {
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