package payment.api.config;

import payment.api.common.resolver.IdResolver;
import payment.api.common.resolver.UsernameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UsernameResolver usernameResolver;
    private final IdResolver idResolver;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(usernameResolver);
        resolvers.add(idResolver);
    }
}
