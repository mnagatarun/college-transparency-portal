package portal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleInterceptor implements HandlerInterceptor {

    private final String requiredRole;

    public RoleInterceptor(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object role = request.getSession().getAttribute("role");
        if (!requiredRole.equals(role)) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}