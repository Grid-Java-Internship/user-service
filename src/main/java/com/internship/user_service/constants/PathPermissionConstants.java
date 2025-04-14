package com.internship.user_service.constants;

import com.internship.authentication_library.config.RequestMatcherInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("security.paths")
public class PathPermissionConstants {
    private List<RequestMatcherInfo> permittedRequestsForAllUsers;
    private List<RequestMatcherInfo> permittedRequestForSuperAdmin;
    private List<RequestMatcherInfo> permittedRequestsForAdminOrSuperAdmin;
    private List<RequestMatcherInfo> permittedRequestsForUsersOrAdminOrSuperAdmin;
}
