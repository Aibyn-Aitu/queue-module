package kz.aitu.abiturqueue.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.aitu.abiturqueue.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer, Filter {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }


    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();


        if(uri.equals("/api/display/tickets/served")) {

        } else {
            if (uri.startsWith("/api")) {
                String code = req.getHeader("code");

                if (code == null || !code.equals("XXXXX")) {
                    log.warn("Unauthorized request from IP: " + req.getRemoteAddr());
                    //res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    //return;
                }
            }
        }

        chain.doFilter(request, response);
    }

}
