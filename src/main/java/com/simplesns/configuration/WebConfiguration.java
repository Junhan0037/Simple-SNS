package com.simplesns.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new ReactResourceResolver();
        registry.addResourceHandler("/**")
                .resourceChain(true)
                .addResolver(resolver);
    }

    public static class ReactResourceResolver implements ResourceResolver {

        private static final String REACT_DIR = "/static/";
        private static final String REACT_STATIC_DIR = "static";

        private final Resource index = new ClassPathResource(REACT_DIR + "index.html");
        private final List<String> staticExtension = Arrays.asList("png", "jpg", "io", "json", "js", "html");

        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            return resolve(requestPath);
        }

        @Override
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            Resource resolvedResource = resolve(resourcePath);
            if (resolvedResource == null) {
                return null;
            }

            try {
                return resolvedResource.getURL().toString();
            } catch (IOException e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath) {
            log.info(requestPath);
            if (requestPath == null) {
                return null;
            }
            if (staticExtension.contains(requestPath) || requestPath.startsWith(REACT_STATIC_DIR)) {
                return new ClassPathResource(REACT_DIR + requestPath);
            } else {
                return index;
            }
        }
    }

}
