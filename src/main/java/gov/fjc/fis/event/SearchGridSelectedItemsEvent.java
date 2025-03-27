package gov.fjc.fis.event;

import org.springframework.context.ApplicationEvent;

public class SearchGridSelectedItemsEvent extends ApplicationEvent {
    private String name;

    public SearchGridSelectedItemsEvent(Object source, String name) {
        super(source);
        this.name = name;
    }
}
