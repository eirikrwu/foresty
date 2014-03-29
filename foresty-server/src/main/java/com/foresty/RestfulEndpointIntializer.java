package com.foresty;

import com.foresty.controller.ControllerConfig;
import com.foresty.filter.GZipRequestFilter;
import com.foresty.quartz.QuartzConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.InputStream;

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

        // configure log4j
        String log4jConfigurationFile;
        if (profile.equals("development")) {
            log4jConfigurationFile = "/log4j-development.properties";
        } else {
            log4jConfigurationFile = "/log4j-production.properties";
        }
        InputStream is = getClass().getResourceAsStream(log4jConfigurationFile);
        PropertyConfigurator.configure(is);

        LOGGER.info("Using profile '" + profile + "'.");

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ControllerConfig.class, QuartzConfig.class);

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/q/*");

        FilterRegistration.Dynamic gzipRequestFilter =
                servletContext.addFilter("gzipRequestFilter", GZipRequestFilter.class);
        gzipRequestFilter.addMappingForUrlPatterns(null, false, "/q/*");
    }
}
