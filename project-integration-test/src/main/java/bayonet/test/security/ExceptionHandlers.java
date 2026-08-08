package bayonet.test.security;

import com.github.yingzhuo.bayonet.security.exception.StatelessJsonWritingExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Component
public class ExceptionHandlers extends StatelessJsonWritingExceptionHandler {

    public ExceptionHandlers(JsonMapper jsonMapper) {
        super(-1, jsonMapper);
    }

    @Override
    protected Object handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        return Map.of("msg", ex.getMessage());
    }

    @Override
    protected Object handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
        return Map.of("msg", ex.getMessage());
    }

    @Override
    protected Object handleRequestRejectedException(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex) {
        return Map.of("msg", ex.getMessage());
    }
}
