package gov.fjc.fis.view.category;

import com.vaadin.flow.data.selection.SelectionEvent;
import gov.fjc.fis.entity.Category;

import gov.fjc.fis.service.AppropriationService;
import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "categories", layout = MainView.class)
@ViewController("fis_Category.list")
@ViewDescriptor("category-list-view.xml")
@LookupComponent("categoriesDataGrid")
@DialogMode(width = "64em")
public class CategoryListView extends StandardListView<Category> {
    @Autowired
    private AppropriationService appropriationService;
    @ViewComponent
    private JmixButton removeBtn;

    @Subscribe("categoriesDataGrid")
    protected void onCategoriesDataGridSelection(final SelectionEvent<DataGrid<Category>, Category> event) {
        var selectedItems = event.getAllSelectedItems();
        if (selectedItems.size() == 1) {
            var selectedItem = selectedItems.stream().findFirst();
            removeBtn.setEnabled(appropriationService.isAppropriationOpen(selectedItem.get()));
        } else {
            removeBtn.setEnabled(false);
        }
    }
}