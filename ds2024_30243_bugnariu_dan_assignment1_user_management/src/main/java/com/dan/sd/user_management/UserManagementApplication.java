package com.dan.sd.user_management;

import com.dan.sd.user_management.controllers.handlers.exceptions.model.DuplicateResourceException;
import com.dan.sd.user_management.dtos.UserDetailsDTO;
import com.dan.sd.user_management.security.JwtGenerator;
import com.dan.sd.user_management.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import java.util.TimeZone;

@SpringBootApplication
@Validated
public class UserManagementApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UserManagementApplication.class);
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        ApplicationContext context = SpringApplication.run(UserManagementApplication.class, args);
        Logger LOGGER = LoggerFactory.getLogger(UserManagementApplication.class);
        // Perform the user check after the application has started
        UserService userService = context.getBean(UserService.class);
        JwtGenerator jwtGenerator = context.getBean(JwtGenerator.class);
        if (userService.findUsers().isEmpty()) {
            try {
                String token = "Bearer " + jwtGenerator.generateToken("DB", 5 * 60 * 1000L);
                userService.insert(new UserDetailsDTO("ADMIN", "admin", "123"), token);
                LOGGER.error("Default admin user created.");
            } catch (DuplicateResourceException e) {
                LOGGER.error("Default admin user already exists.");
            }
        }
    }
}
