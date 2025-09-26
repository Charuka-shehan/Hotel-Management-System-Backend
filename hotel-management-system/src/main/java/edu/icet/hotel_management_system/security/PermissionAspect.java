package edu.icet.hotel_management_system.security;

import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.repository.UserRepository;
import edu.icet.hotel_management_system.service.RolePermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRepository userRepository;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new AccessDeniedException("User not found");
        }

        String requiredPermission = requirePermission.value();
        if (!rolePermissionService.hasPermission(user.getRole(), requiredPermission)) {
            throw new AccessDeniedException("Insufficient permissions: " + requiredPermission);
        }

        return joinPoint.proceed();
    }
}
