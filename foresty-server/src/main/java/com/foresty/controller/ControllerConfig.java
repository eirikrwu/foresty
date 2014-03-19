package com.foresty.controller;

import com.foresty.DomainConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by EveningSun on 14-3-18.
 */
@Configuration
@EnableWebMvc
@Import(DomainConfig.class)
@ComponentScan("com.foresty.controller")
public class ControllerConfig {
}
