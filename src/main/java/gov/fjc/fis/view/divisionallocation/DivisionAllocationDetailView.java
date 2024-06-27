package gov.fjc.fis.view.divisionallocation;

import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.DivisionAllocation;

import gov.fjc.fis.service.CategoryService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "divisionAllocations/:id", layout = MainView.class)
@ViewController("fis_DivisionAllocation.detail")
@ViewDescriptor("division-allocation-detail-view.xml")
@EditedEntityContainer("divisionAllocationDc")
public class DivisionAllocationDetailView extends StandardDetailView<DivisionAllocation> {
    @Autowired
    private EntityStates entityStates;
    @Autowired
    private CategoryService categoryService;
    @ViewComponent
    private EntityComboBox<Category> categoryField;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        var allocation = getEditedEntity();
        if (entityStates.isNew(allocation)) {
            categoryField.focus();
        } else {
            categoryField.setReadOnly(true);
        }

    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    private List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        var division = getEditedEntity().getDivision();
        var allCategories = categoryService.getCategoriesForBfy(division.getAppropriation());
        var allocations = division.getAllocations();
        if (allocations == null) {
            return allCategories;
        } else {
            var usedCategories = allocations.stream().map(DivisionAllocation::getCategory).toList();
            return allCategories.stream().filter(category -> !usedCategories.contains(category)).toList();
        }
    }

    @Install(to = "categoryField", subject = "itemLabelGenerator")
    private Object categoryFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }
}