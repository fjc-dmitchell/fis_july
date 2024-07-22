package gov.fjc.fis.view.mainviewfragment;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.service.UserMessageService;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@FragmentDescriptor("main-view-fragment.xml")
public class MainViewFragment extends Fragment<VerticalLayout> {
    @Autowired
    private UserMessageService userMessageService;
    @ViewComponent
    private VerticalLayout messageBox;

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostInit(final View.InitEvent event) {
        var userMessage = userMessageService.getCurrentMessage();
        if (userMessage.getMessage() != null && !userMessage.getMessage().isEmpty()) {
            var message = "<div>".concat(userMessage.getMessage()).concat("</div>");
            messageBox.add(new Html(message));
        }
    }
}