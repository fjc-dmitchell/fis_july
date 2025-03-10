package gov.fjc.fis.view.usersettingsitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.settings.UserSettingsCache;
import io.jmix.flowui.view.*;
import io.jmix.flowuidata.entity.UserSettingsItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


@Route(value = "userSettingsItems", layout = MainView.class)
@ViewController(id = "flowui_UserSettingsItem.list")
@ViewDescriptor(path = "user-settings-item-list-view.xml")
@LookupComponent("userSettingsItemsDataGrid")
@DialogMode(width = "64em")
public class UserSettingsItemListView extends StandardListView<UserSettingsItem> {
    @ViewComponent
    private DataGrid<UserSettingsItem> userSettingsItemsDataGrid;
    @Autowired
    private UserSettingsCache userSettingsCache;

    @Subscribe(id = "removeButton", subject = "clickListener")
    protected void onRemoveButtonClick(final ClickEvent<JmixButton> event) {
        var viewId = Objects.requireNonNull(userSettingsItemsDataGrid.getSingleSelectedItem()).getKey();
        userSettingsCache.delete(viewId);
    }
}