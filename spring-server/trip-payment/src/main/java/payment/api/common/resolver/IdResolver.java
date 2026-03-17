package payment.api.common.resolver;

import payment.api.common.resolver.annotation.Id;
import payment.api.security.basic.userdetail.CustomUserDetails;
import core.common.Status;
import core.common.exception.CommonException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class IdResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasString = parameter.getParameterType().isAssignableFrom(Long.class);
        boolean hasIdAnnotation = parameter.hasParameterAnnotation(Id.class);
        return hasString && hasIdAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new CommonException(Status.NOT_AUTHORIZED);
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getJwtSubject().getId();

    }
}
