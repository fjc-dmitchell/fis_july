package gov.fjc.fis.view.search;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.Group;
import gov.fjc.fis.entity.ObjectClass;
import io.jmix.core.querycondition.Condition;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.Target;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewComponent;

import java.util.ArrayList;
import java.util.List;

@FragmentDescriptor("entity-search-fragment.xml")
public abstract class EntitySearchFragment extends Fragment<VerticalLayout> {
    @ViewComponent
    private VerticalLayout root;

    private final List<Condition> propertyFilterConditions = new ArrayList<>();
    private final List<PropertyFilter<?>> propertyFilters = new ArrayList<>();

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected final void onHostReady(final View.ReadyEvent event) {
        for (var component : UiComponentUtils.getComponents(root)) {
            if (component instanceof PropertyFilter<?> propertyFilter) {
                addFilterCondition(propertyFilter);
            }
        }
        additionalFragmentActions();
    }

    private void addFilterCondition(PropertyFilter<?> filter) {
        filter.setAutoApply(false);
//        filter.setLabelPosition(SupportsLabelPosition.LabelPosition.TOP);
        propertyFilters.add(filter);
        propertyFilterConditions.add(filter.getQueryCondition());
    }

    protected void additionalFragmentActions() {
        // subclass may need to perform additional actions
    }

    public final List<Condition> getPropertyFilterConditions() {
        return propertyFilterConditions;
    }

    public void clearPropertyFilters() {
        for (PropertyFilter<?> filter : propertyFilters) {
            filter.clear();
        }
    }

    public void addCategoryObjectClass(EntityComboBox<Category> categorySearchField,
                                       EntityComboBox<ObjectClass> objectClassSearchField) {
        // by default, do nothing. Subclasses can override this method.
    }

    public void addBranchGroup(EntityComboBox<Branch> branchSearchField,
                               EntityComboBox<Group> groupSearchField) {
        // by default, do nothing. Subclasses can override this method.
    }
}