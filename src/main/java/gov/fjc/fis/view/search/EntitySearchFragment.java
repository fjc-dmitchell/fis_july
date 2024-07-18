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

    private final List<Condition> customConditions = new ArrayList<>();
    private final List<PropertyFilter<?>> propertyFilters = new ArrayList<>();

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        for(var component : UiComponentUtils.getComponents(root)){
            if(component instanceof PropertyFilter<?>){
                addFilterCondition((PropertyFilter<?>) component);
            }
        }
    }

    private void addFilterCondition(PropertyFilter<?> filter) {
        filter.setAutoApply(false);
//        filter.setLabelPosition(SupportsLabelPosition.LabelPosition.TOP);
        propertyFilters.add(filter);
        customConditions.add(filter.getQueryCondition());
    }

    public List<Condition> getCustomConditions() {
        return customConditions;
    }

    public void clearSearchFilters() {
        for (PropertyFilter<?> filter : propertyFilters) {
            filter.clear();
        }
    }

    /**
     * clear just the parameter, not the filter value
     */
//    public void clearSearchConditions() {
//        for(PropertyFilter<?> filter : propertyFilters){
//            filter.getQueryCondition().setParameterValue(null);
//        }
//    }

    /**
     * reset the parameters to the saved filter values. This is so we can switch between tabs without losing filter values
     */
//    public void resetSearchConditions() {
//        for(PropertyFilter<?> filter : propertyFilters){
//           if(filter.getValueComponent().isEmpty()) {
//               filter.getQueryCondition().setParameterValue(filter.getValueComponent().getValue());
//           }
//        }
//    }

    public void addCategoryObjectClass(EntityComboBox<Category> category, EntityComboBox<ObjectClass> objectClass) {
        // by default, do nothing. Subclasses can override this method.
    }

    public void addBranchGroup(EntityComboBox<Branch> branch, EntityComboBox<Group> group) {
        // by default, do nothing. Subclasses can override this method.
    }
}