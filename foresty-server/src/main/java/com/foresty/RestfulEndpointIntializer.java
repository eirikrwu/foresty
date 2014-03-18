package com.foresty;

import org.apache.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by ericwu on 3/16/14.
 */
public class RestfulEndpointIntializer implements WebApplicationInitializer {
    private static final Logger LOGGER = Logger.getLogger(RestfulEndpointIntializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String profile = System.getProperty("spring.profiles.default");
        if (profile == null) {
            profile = "development";
        }
        servletContext.setInitParameter("spring.profiles.default", profile);
        LOGGER.info("Using profile '" + profile + "'.");

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(Config.class);

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
