package gov.fjc.fis.bean;

import io.jmix.core.Messages;
import io.jmix.core.Resources;
import io.jmix.core.common.xmlparsing.Dom4jTools;
import io.jmix.flowui.UiComponents;
import org.springframework.stereotype.Component;

@Component("fis_LandingPageGenerator")
public class LandingPageGenerator {

    private final Dom4jTools dom4jTools;
    private final UiComponents uiComponents;
    private final Resources resources;
    private final Messages messages;

    public LandingPageGenerator(Dom4jTools dom4jTools, UiComponents uiComponents, Resources resources,
                                 Messages messages) {
        this.dom4jTools = dom4jTools;
        this.uiComponents = uiComponents;
        this.resources = resources;
        this.messages = messages;
    }
}