package gov.fjc.fis.view.search;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.ObjectClass;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.Target;
import io.jmix.flowui.view.ViewComponent;

@FragmentDescriptor("invoice-search-fragment.xml")
public class InvoiceSearchFragment extends EntitySearchFragment {
    @ViewComponent
    private PropertyFilter<Boolean> obligationStatusSearch;
    @ViewComponent
    private HorizontalLayout mocBox;
    @ViewComponent
    private HorizontalLayout bocBox;

    private EntityComboBox<Category> categorySearchField;
    private EntityComboBox<ObjectClass> objectClassSearchField;

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostAttach(final AttachEvent event) {
        ((JmixSelect<Boolean>) obligationStatusSearch.getValueComponent()).setItemLabelGenerator(status -> {
            if (status == null) {
                return "";
            }
            return status ? "Open" : "Closed";
        });
    }

    @Override
    public void addCategoryObjectClass(EntityComboBox<Category> category, EntityComboBox<ObjectClass> objectClass) {
        categorySearchField = category;
        mocBox.add(categorySearchField);
        objectClassSearchField = objectClass;
        bocBox.add(objectClassSearchField);
    }

    @Override
    public void clearSearchFilters() {
        super.clearSearchFilters();
        categorySearchField.setValue(null);
        objectClassSearchField.setValue(null);
    }
}