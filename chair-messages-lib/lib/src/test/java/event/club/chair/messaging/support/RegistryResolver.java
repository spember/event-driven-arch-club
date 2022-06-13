package event.club.chair.messaging.support;

import event.club.chair.messaging.MessageTypeRegistry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class RegistryResolver implements ParameterResolver {

    private final MessageTypeRegistry registry;

    public RegistryResolver() {
        this.registry = new MessageTypeRegistry("event.club.chair");
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == MessageTypeRegistry.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.registry;
    }
}
