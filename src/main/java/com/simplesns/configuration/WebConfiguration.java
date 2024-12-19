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

/**
 * Spring Web MVC 설정 클래스
 * 애플리케이션의 정적 파일 제공 및 URL 요청 처리 로직을 설정
 */
@Configuration
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 정적 리소스를 처리하기 위한 ResourceHandler 를 등록
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 리소스 요청을 처리하기 위한 커스텀 ResourceResolver 생성
        ResourceResolver resolver = new ReactResourceResolver();

        // 모든 요청에 대해 이 Resolver 를 사용하도록 설정
        registry.addResourceHandler("/**")
                .resourceChain(true)
                .addResolver(resolver);
    }

    /**
     * 애플리케이션의 정적 리소스를 처리하는 ResourceResolver 구현
     */
    public static class ReactResourceResolver implements ResourceResolver {

        // 애플리케이션의 정적 리소스가 위치한 디렉토리
        private static final String REACT_DIR = "/static/";
        private static final String REACT_STATIC_DIR = "static";

        // 애플리케이션의 index.html 파일
        private final Resource index = new ClassPathResource(REACT_DIR + "index.html");
        // 정적 파일의 확장자 목록
        private final List<String> staticExtension = Arrays.asList("png", "jpg", "io", "json", "js", "html");

        /**
         * 리소스 요청을 처리하는 메서드
         * 요청된 경로를 기준으로 적합한 리소스 반환
         */
        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            return resolve(requestPath);
        }

        /**
         * URL 경로를 처리하여 정적 리소스 또는 index.html 의 경로를 반환
         */
        @Override
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            // 요청된 리소스 확인
            Resource resolvedResource = resolve(resourcePath);

            if (resolvedResource == null) {
                return null;
            }

            try {
                return resolvedResource.getURL().toString(); // 리소스의 URL 반환
            } catch (IOException e) {
                return resolvedResource.getFilename(); // URL 을 가져올 수 없으면 파일 이름 반환
            }
        }

        /**
         * 요청된 경로를 기준으로 정적 리소스 또는 index.html 반환
         */
        private Resource resolve(String requestPath) {
            log.info(requestPath);

            if (requestPath == null) {
                return null;
            }

            // 요청 경로가 정적 파일 확장자를 포함하거나 정적 리소스가 위치한 디렉토리로 시작하는 경우
            if (staticExtension.contains(requestPath) || requestPath.startsWith(REACT_STATIC_DIR)) {
                return new ClassPathResource(REACT_DIR + requestPath); // 정적 리소스 반환
            }

            return index; // 정적 리소스가 아니면 index.html 반환
        }
    }

}
