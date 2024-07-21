package gov.fjc.fis.view.usermessage;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.UserMessage;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "userMessages/:id", layout = MainView.class)
@ViewController("fis_UserMessage.detail")
@ViewDescriptor("user-message-detail-view.xml")
@EditedEntityContainer("userMessageDc")
public class UserMessageDetailView extends StandardDetailView<UserMessage> {
}