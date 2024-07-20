package gov.fjc.fis.view.activityfragment;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Group;
import gov.fjc.fis.service.ActivityService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@FragmentDescriptor("activity-fragment.xml")
public class ActivityFragment extends Fragment<VerticalLayout> {

    @ViewComponent
    private CollectionLoader<Activity> activitiesDl;
    @Autowired
    private ActivityService activityService;
    private Object entity;

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        activitiesDl.load();
    }

    @Install(to = "activitiesDl", target = Target.DATA_LOADER)
    protected List<Activity> activitiesDlLoadDelegate(final LoadContext<Activity> loadContext) {
        List<Activity> activities = new ArrayList<>();
        switch (entity.getClass().getSimpleName()) {
            case "Group" -> activities = activityService.getActivities((Group) entity);
            case "Branch" -> activities = activityService.getActivities((Branch) entity);
        }
        return activities;
    }
}