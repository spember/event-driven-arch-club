package event.club.chair.messaging;

import event.club.chair.messaging.exceptions.RegistryCollisionException;
import event.club.chair.messaging.messages.DomainMessage;
import event.club.chair.messaging.messages.HeaderInfo;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contains a bi-directional lookup of DomainMessage Class to header key alias value and header key alias value to
 * Class.
 *
 * It is recommended to create and use as a Singleton, as this will scan the Classpath on creation throw exceptions
 * if conflicting aliases are found.
 *
 */
public class MessageTypeRegistry {

    private final Map<String, Class<? extends DomainMessage>> aliasToClassLookup = new HashMap<>();
    private final Map<Class<? extends DomainMessage>, String> classToAliasLookup = new HashMap<>();

    private static Logger log = LoggerFactory.getLogger(MessageTypeRegistry.class);

    public MessageTypeRegistry(String packageName) {
        findEventClassesInPackageHierarchy(packageName)
                .forEach(this::register);
        log.info("Domain Message Registry loaded with {} messages", classToAliasLookup.size());
    }

    public Optional<String> getAliasForMessage(Class<? extends DomainMessage> domainClass) {
        return Optional.ofNullable(classToAliasLookup.get(domainClass));
    }

    public Optional<Class<? extends DomainMessage>> getMessageForAlias(String alias) {
        return Optional.ofNullable(aliasToClassLookup.get(alias));
    }

    public void register(Class<? extends DomainMessage> potentialMessageClass) {
        addToAliasLookup(potentialMessageClass); //if there's an alias mismatch this should trip first
        addToClassLookup(potentialMessageClass);
    }


    private Set<Class<? extends DomainMessage>> findEventClassesInPackageHierarchy(String packagePath) {
        return new Reflections(packagePath).getSubTypesOf(DomainMessage.class);
    }

    private <DM extends DomainMessage> void addToAliasLookup(Class<DM> domainClass) {
        // grab all aliases and create records for each
        calculateAlias(domainClass).forEach(alias -> {
            if (aliasToClassLookup.containsKey(alias)) {
                throw new RegistryCollisionException("There already exists an alias of '" + alias
                        + "' within the registry");
            }
            aliasToClassLookup.put(alias, domainClass);
        });
    }

    private <DM extends DomainMessage> void addToClassLookup(Class<DM> domainClass) {
        // use the first alias, or the SimpleName
        String alias = calculateAlias(domainClass).get(0);
        classToAliasLookup.put(domainClass, alias);
    }

    private List<String> calculateAlias(Class<? extends DomainMessage> eClass) {
        List<String> aliases = new ArrayList<>();
        if (!eClass.isAnnotationPresent(HeaderInfo.class)) {
            aliases.add(eClass.getSimpleName());
        } else {
            HeaderInfo annotation = eClass.getAnnotation(HeaderInfo.class);
            if (annotation.aliases().length == 0) {
                aliases.add(eClass.getSimpleName());
            } else {
                aliases.addAll(List.of(annotation.aliases()));
            }
        }
        return aliases;
    }
}
