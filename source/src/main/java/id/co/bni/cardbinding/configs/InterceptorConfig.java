package id.co.bni.cardbinding.configs;

import id.co.bni.cardbinding.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Component
@Slf4j
public class InterceptorConfig implements HandlerInterceptor {
    @Value("${spring.application.name}")
    private String app;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String notFound = """
                 {"isSuccess":false,"responseCode":"02,"payload":null,"responseMessage":"Not found"}
                """;
        if(request.getRequestURI().startsWith("/_/health")) {return true;}
        String correlationId = StringUtil.setUuid();
        if (request.getMethod().equals("POST") || (request.getMethod().equals("GET"))) {
            Long start = System.nanoTime();
            MDC.put("start", String.valueOf(start));
            MDC.put("app", app);
            MDC.put("corrId", correlationId);
            log.info("========    START PROCESS   ==========");
            log.info("{} {} {}://{}:{}{}",
                    request.getRemoteAddr(),
                    request.getMethod(),
                    request.getScheme(),
                    request.getServerName(),
                    request.getServerPort(),
                    request.getRequestURI());
            response.setHeader("X-Corr-Id", correlationId);
            return true;
        }
        MDC.put("app", app);
        MDC.put("corrId", correlationId);
        log.info("{} {} {}://{}:{}{}",
                request.getRemoteAddr(),
                request.getMethod(),
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                request.getRequestURI());
        response.setHeader("X-Corr-Id", correlationId);
        response.setStatus(404);
        response.setContentType("application/json");
        response.getWriter().write(notFound);
        log.warn(notFound);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(!request.getRequestURI().startsWith("/_/health")) {
            Long start = Long.parseLong(MDC.get("start"));
            Long end = System.nanoTime();
            long elapsedTimeInMillis = (end - start) / 1_000_000;
            String elapsedTime;
            if (elapsedTimeInMillis > 1000) {
                elapsedTime = (elapsedTimeInMillis / 1000) + " seconds";
            } else {
                elapsedTime = elapsedTimeInMillis + " ms";
            }
            log.info("========    END   PROCESS   ==========" + " " + elapsedTime);
            MDC.remove("start");
            MDC.remove("app");
            MDC.remove("corrId");
        }
    }
}
