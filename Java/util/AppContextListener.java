package oes.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Online Quiz System: Application starting up...");
        DbSchemaInitializer.initializeTables();
        System.out.println("Online Quiz System: Startup initialization complete.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Online Quiz System: Application stopping...");
    }
}
