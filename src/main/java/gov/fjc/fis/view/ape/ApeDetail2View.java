package gov.fjc.fis.view.ape;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Paragraph;
import gov.fjc.fis.entity.form.Ape;

import gov.fjc.fis.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.textfield.JmixBigDecimalField;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;

@Route(value = "apes2/:id", layout = MainView.class)
@ViewController("fis_Ape2.detail")
@ViewDescriptor("ape-detail2-view.xml")
@EditedEntityContainer("apeDc")
@PrimaryDetailView(Ape.class)
public class ApeDetail2View extends StandardDetailView<Ape> {
    @ViewComponent
    private Paragraph createdByString;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        createdByString.setText(getEditedEntity().getCreatedByString());
    }
}