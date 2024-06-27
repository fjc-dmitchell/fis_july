package gov.fjc.fis.view.activity;

import gov.fjc.fis.entity.Activity;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "activities", layout = MainView.class)
@ViewController("fis_Activity.list")
@ViewDescriptor("activity-list-view.xml")
@LookupComponent("activitiesDataGrid")
@DialogMode(width = "64em")
public class ActivityListView extends StandardListView<Activity> {
}