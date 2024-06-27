package gov.fjc.fis.view.category;

import gov.fjc.fis.entity.Category;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "categories/:id", layout = MainView.class)
@ViewController("fis_Category.detail")
@ViewDescriptor("category-detail-view.xml")
@EditedEntityContainer("categoryDc")
public class CategoryDetailView extends StandardDetailView<Category> {
}