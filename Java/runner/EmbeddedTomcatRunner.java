package runner;

import java.io.File;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class EmbeddedTomcatRunner {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // use default 8080
            }
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // trigger connector creation

        String webappDirLocation = new File("web").getAbsolutePath();
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDirLocation);
        System.out.println("Configuring app with basedir: " + webappDirLocation);

        // Declare an alternative location for classes
        File additionWebInfClasses = new File("build/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        tomcat.start();
        System.out.println("Tomcat started on http://localhost:" + port);
        tomcat.getServer().await();
    }
}
