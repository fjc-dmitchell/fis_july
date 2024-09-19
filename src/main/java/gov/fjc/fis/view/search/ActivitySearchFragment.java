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

    @Override
    public void addCategoryObjectClass(EntityComboBox<Category> categorySearchField,
                                       EntityComboBox<ObjectClass> objectClassSearchField) {
        mocBox.add(categorySearchField);
        bocBox.add(objectClassSearchField);
    }

    @Override
    public void addBranchGroup(EntityComboBox<Branch> branchSearchField,
                               EntityComboBox<Group> groupSearchField) {
        branchBox.add(branchSearchField);
        groupBox.add(groupSearchField);
    }
}