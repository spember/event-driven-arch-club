package event.club.chair.messaging.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ObjectMapperParameterResolver implements ParameterResolver {

    protected static ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == ObjectMapper.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return mapper;
    }
}
