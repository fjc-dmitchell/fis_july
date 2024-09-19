package gov.fjc.fis.view.search;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.ObjectClass;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.*;

@FragmentDescriptor("obligation-search-fragment.xml")
public class ObligationSearchFragment extends EntitySearchFragment {
    @ViewComponent
    private PropertyFilter<Boolean> statusSearch;
    @ViewComponent
    private HorizontalLayout mocBox;
    @ViewComponent
    private HorizontalLayout bocBox;

//    private EntityComboBox<Category> categorySearchField;
//    private EntityComboBox<ObjectClass> objectClassSearchField;

    @Override
    protected void additionalFragmentActions() {
        ((JmixSelect<Boolean>) statusSearch.getValueComponent()).setItemLabelGenerator(status -> {
            return status == null ? "" : status ? "Open" : "Closed";
//            if (status == null) {
//                return "";
//            }
//            return status ? "Open" : "Closed";
        });
    }

    @Override
    public void addCategoryObjectClass(EntityComboBox<Category> categorySearchField,
                                       EntityComboBox<ObjectClass> objectClassSearchField) {
//        categorySearchField = category;
//        mocBox.add(categorySearchField);
//        objectClassSearchField = objectClass;
//        bocBox.add(objectClassSearchField);
        mocBox.add(categorySearchField);
        bocBox.add(objectClassSearchField);
    }

//    @Override
//    public void clearPropertyFilters() {
//        super.clearPropertyFilters();
//        categorySearchField.setValue(null);
//        objectClassSearchField.setValue(null);
//    }
}