package gov.fjc.fis.view.search;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.Group;
import gov.fjc.fis.entity.ObjectClass;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.ViewComponent;

@FragmentDescriptor("activity-search-fragment.xml")
public class ActivitySearchFragment extends EntitySearchFragment {

    @ViewComponent
    private HorizontalLayout branchBox;
    @ViewComponent
    private HorizontalLayout groupBox;
    @ViewComponent
    private HorizontalLayout mocBox;
    @ViewComponent
    private HorizontalLayout bocBox;

    private EntityComboBox<Category> categorySearchField;
    private EntityComboBox<ObjectClass> objectClassSearchField;
    private EntityComboBox<Branch> branchSearchField;
    private EntityComboBox<Group> groupSearchField;

    @Override
    public void addCategoryObjectClass(EntityComboBox<Category> category, EntityComboBox<ObjectClass> objectClass) {
        categorySearchField = category;
        mocBox.add(categorySearchField);
        objectClassSearchField = objectClass;
        bocBox.add(objectClassSearchField);
    }

    @Override
    public void addBranchGroup(EntityComboBox<Branch> branch, EntityComboBox<Group> group) {
        branchSearchField = branch;
        branchBox.add(branchSearchField);
        groupSearchField = group;
        groupBox.add(groupSearchField);
    }

    @Override
    public void clearSearchFilters() {
        super.clearSearchFilters();
        categorySearchField.setValue(null);
        objectClassSearchField.setValue(null);
        branchSearchField.setValue(null);
        groupSearchField.setValue(null);
    }
}