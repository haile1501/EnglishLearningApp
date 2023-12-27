package org.ict.client;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ict.client.models.User;

@Getter
public class UserContext {
    private static final Logger logger = LogManager.getLogger(UserContext.class);

    private User user;

    private UserContext() {

    }

    private static final class InstanceHolder {
        private static final UserContext instance = new UserContext();
    }

    public static UserContext getInstance() {

        return InstanceHolder.instance;
    }

    public void initializeContext(User user) {
        this.user = user;
    }

}
