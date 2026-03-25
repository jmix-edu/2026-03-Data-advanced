package com.company.jmixpmdata.listener;

import com.company.jmixpmdata.security.FullAccessRole;
import io.jmix.core.session.SessionData;
import io.jmix.security.role.assignment.RoleAssignment;
import io.jmix.security.role.assignment.RoleAssignmentRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class AuthenticationEventListener {

    private final ObjectProvider<SessionData> sessionDataObjectProvider;
    private final RoleAssignmentRepository roleAssignmentRepository;

    public AuthenticationEventListener(ObjectProvider<SessionData> sessionDataObjectProvider, RoleAssignmentRepository roleAssignmentRepository) {
        this.sessionDataObjectProvider = sessionDataObjectProvider;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    @EventListener
    public void onInteractiveAuthenticationSuccess(final InteractiveAuthenticationSuccessEvent event) {
        SessionData sessionData = sessionDataObjectProvider.getIfAvailable();
        if (sessionData != null) {
            UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
            RoleAssignment supposedFullAccessAssignment = roleAssignmentRepository
                    .getAssignmentsByUsername(userDetails.getUsername())
                    .stream().filter(roleAssignment -> roleAssignment.getRoleCode().equals(FullAccessRole.CODE))
                    .findFirst()
                    .orElse(null);

            sessionData.setAttribute("isAdmin", supposedFullAccessAssignment != null);
        }
    }
}