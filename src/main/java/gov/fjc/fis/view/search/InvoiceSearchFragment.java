package gov.fjc.fis.view.search;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.ObjectClass;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.ViewComponent;

import java.util.Map;

@FragmentDescriptor("invoice-search-fragment.xml")
public class InvoiceSearchFragment extends EntitySearchFragment {
    @ViewComponent
    private HorizontalLayout mocBox;
    @ViewComponent
    private HorizontalLayout bocBox;
    @ViewComponent
    private PropertyFilter<Object> invoiceNumberSearch;

    @ViewComponent
    private PropertyFilter<Object> invoiceDateSearch;
    @ViewComponent
    private PropertyFilter<Object> invoicePaidDateSearch;

    @ViewComponent
    private PropertyFilter<Object> vendorSearch;
    @ViewComponent
    private PropertyFilter<Boolean> obligationStatusSearch;
    @ViewComponent
    private PropertyFilter<Object> docnumSearch;
    @ViewComponent
    private PropertyFilter<Object> amountSearch;

    @Override
    protected void additionalFragmentActions() {
        ((JmixSelect<Boolean>) obligationStatusSearch.getValueComponent())
                .setItemLabelGenerator(status -> status == null ? "" : status ? "Open" : "Closed");
    }

    @Override
    public void setPropertyFilters(Map<String, Object> filters) {
        for (var entry : filters.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            switch (key) {
                case "invoiceNumber":
                    invoiceNumberSearch.setValue(value);
                    break;
                case "amount":
                    amountSearch.setValue(value);
                    break;
                case "amount_op":
                    amountSearch.setOperation((PropertyFilter.Operation) value);
                    break;
                case "invoiceDate":
                    invoiceDateSearch.setValue(value);
                    break;
                case "invoiceDate_op":
                    invoiceDateSearch.setOperation((PropertyFilter.Operation) value);
                    break;
                case "paymentDate":
                    invoicePaidDateSearch.setValue(value);
                    break;
                case "paymentDate_op":
                    invoicePaidDateSearch.setOperation((PropertyFilter.Operation) value);
                    break;
                case "obligation.documentNumber":
                    docnumSearch.setValue(value);
                    break;
                case "obligation.vendor":
                    vendorSearch.setValue(value);
                    break;
                case "obligation.status":
                    obligationStatusSearch.setValue((Boolean) value);
                    break;
            }
        }
    }

    @Override
    public void addCategoryObjectClass(EntityComboBox<Category> categorySearchField,
                                       EntityComboBox<ObjectClass> objectClassSearchField) {
        mocBox.add(categorySearchField);
        bocBox.add(objectClassSearchField);
    }
}