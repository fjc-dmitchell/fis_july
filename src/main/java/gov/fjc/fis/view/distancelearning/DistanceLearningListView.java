package gov.fjc.fis.view.distancelearning;

import gov.fjc.fis.entity.form.DistanceLearning;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "distanceLearnings", layout = MainView.class)
@ViewController("fis_DistanceLearning.list")
@ViewDescriptor("distance-learning-list-view.xml")
@LookupComponent("distanceLearningsDataGrid")
@DialogMode(width = "64em")
public class DistanceLearningListView extends StandardListView<DistanceLearning> {
}