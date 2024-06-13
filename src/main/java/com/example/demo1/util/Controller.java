package com.example.demo1.util;

import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.IntoResponse;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Data
public class Controller {
    private static final Logger logger = LogManager.getLogger();

    private List<Method> getMethods;
    private List<Method> postMethods;

    protected Controller() {
        final var getMethods = Arrays.stream(this.getClass().getMethods())
            .filter(method -> method.isAnnotationPresent(GetMapping.class))
            .toList();

        final var postMethods = Arrays.stream(this.getClass().getMethods())
            .filter(method -> method.isAnnotationPresent(PostMapping.class))
            .toList();

        for (final var method : getMethods) {
            if (!IntoResponse.class.isAssignableFrom(method.getReturnType())) {
                throw new RuntimeException("Invalid return type of '" + method.getName() + "'. Expected 'IntoResponse' interface");
            }

            // TODO check params or make the '@Autowire' attribute...
//            final var params = method.getParameters();

//            if (method.getAnnotation(AuthRequired.class) == null) {
//                if (params.length != 1) {
//                    throw new RuntimeException("Excepted only one 'RequestContext' as a first parameter  '" + method.getName() + "'. Expected 'IntoResponse' interface");
//                }
//            } else {
//                if (params.length != 2) {
//
//                }
//
//                params[0].getType().equals(RequestContext.class)
//            }
        }

        for (final var method : postMethods) {
            if (!IntoResponse.class.isAssignableFrom(method.getReturnType())) {
                throw new RuntimeException("Invalid return type of '" + method.getName() + "'. Expected 'IntoResponse' interface");
            }
        }

        this.getMethods = getMethods;
        this.postMethods = postMethods;

        logger.info("All methods: " + Arrays.toString(this.getClass().getMethods()));
        logger.info("Get methods: " + getMethods);
        logger.info("Post methods: " + postMethods);
    }
}