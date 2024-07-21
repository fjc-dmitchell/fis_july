package gov.fjc.fis.service;

import gov.fjc.fis.entity.UserMessage;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("fis_UserMessageService")
public class UserMessageService {
    @Autowired
    private DataManager dataManager;

    public UserMessage getCurrentMessage() {
        return dataManager.load(UserMessage.class)
                .query("SELECT e FROM fis_UserMessage e"
                        + " WHERE e.published = TRUE"
                        + " ORDER BY e.postDate DESC")
                .maxResults(1)
                .optional()
                .orElse(dataManager.create(UserMessage.class));
    }
}