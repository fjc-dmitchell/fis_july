package gov.fjc.fis.view.employee;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.personnel.Employee;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "employees", layout = MainView.class)
@ViewController("fis_Employee.list")
@ViewDescriptor("employee-list-view.xml")
@LookupComponent("employeesDataGrid")
@DialogMode(width = "64em")
public class EmployeeListView extends StandardListView<Employee> {
}