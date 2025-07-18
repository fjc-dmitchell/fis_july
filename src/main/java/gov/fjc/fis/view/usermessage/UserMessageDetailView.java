package gov.fjc.fis.view.usermessage;

import com.vaadin.flow.router.Route;
import gov.fjc.fis.entity.UserMessage;
import gov.fjc.fis.event.UserMessageSavedEvent;
import gov.fjc.fis.view.main.MainView;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "userMessages/:id", layout = MainView.class)
@ViewController("fis_UserMessage.detail")
@ViewDescriptor("user-message-detail-view.xml")
@EditedEntityContainer("userMessageDc")
public class UserMessageDetailView extends StandardDetailView<UserMessage> {
//    @Autowired
//    private UiEventPublisher uiEventPublisher;
//
//    @Subscribe(target = Target.DATA_CONTEXT)
//    protected void onPostSave(final DataContext.PostSaveEvent event) {
//        uiEventPublisher.publishEvent(new UserMessageSavedEvent(this, "userMessageSaved"));
//    }
}