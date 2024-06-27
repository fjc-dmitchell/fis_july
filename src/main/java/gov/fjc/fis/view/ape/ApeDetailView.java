package gov.fjc.fis.view.ape;

import com.vaadin.flow.component.html.Paragraph;
import gov.fjc.fis.entity.form.Ape;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "apes/:id", layout = MainView.class)
@ViewController("fis_Ape.detail")
@ViewDescriptor("ape-detail-view.xml")
@EditedEntityContainer("apeDc")
public class ApeDetailView extends StandardDetailView<Ape> {
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        createdByString.setText(getEditedEntity().getCreatedByString());
    }
}