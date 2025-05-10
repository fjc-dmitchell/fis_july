package gov.fjc.fis.view.department;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.Department;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "departments/:id", layout = MainView.class)
@ViewController(id = "fis_Department.detail")
@ViewDescriptor(path = "department-detail-view.xml")
@EditedEntityContainer("departmentDc")
public class DepartmentDetailView extends StandardDetailView<Department> {
}