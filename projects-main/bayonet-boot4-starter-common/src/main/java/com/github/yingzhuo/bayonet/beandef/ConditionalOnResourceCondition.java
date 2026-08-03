package com.github.yingzhuo.bayonet.beandef;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ConditionalOnResource} 条件注解的求值实现。
 *
 * @author 应卓
 * @since 4.1.1
 */
class ConditionalOnResourceCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var loader = context.getResourceLoader();

        MultiValueMap<String, @Nullable Object> attributes = metadata
                .getAllAnnotationAttributes(ConditionalOnResource.class.getName(), true);
        Assert.state(attributes != null, "'attributes' must not be null");

        var logic = (Logic) attributes.getFirst("logic");

        var locations = new ArrayList<String>();
        List<@Nullable Object> resources = attributes.get("resources");
        Assert.state(resources != null, "'resources' must not be null");

        collectValues(locations, resources);
        Assert.state(!locations.isEmpty(),
                "@ConditionalOnResource annotations must specify at least one resource location");

        var missing = new ArrayList<String>();
        var matching = new ArrayList<String>();
        for (String location : locations) {
            String resource = context.getEnvironment().resolvePlaceholders(location);
            if (!loader.getResource(resource).exists()) {
                missing.add(location);
            } else {
                matching.add(location);
            }
        }

        if (logic == Logic.AND) {
            if (!missing.isEmpty()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnResource.class)
                        .didNotFind("resource", "resources")
                        .items(ConditionMessage.Style.QUOTE, missing));
            }
            return ConditionOutcome.match(ConditionMessage.forCondition(ConditionalOnResource.class)
                    .found("location", "locations")
                    .items(locations));
        } else {
            if (matching.isEmpty()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnResource.class)
                        .didNotFind("resource", "resources")
                        .items(ConditionMessage.Style.QUOTE, missing));
            } else {
                return ConditionOutcome.match(ConditionMessage.forCondition(ConditionalOnResource.class)
                        .found("location", "locations")
                        .items(matching));
            }
        }
    }

    private void collectValues(List<String> names, List<@Nullable Object> resources) {
        for (Object resource : resources) {
            if (resource instanceof String[] items) {
                Collections.addAll(names, items);
            } else if (resource instanceof String location) {
                names.add(location);
            }
        }
    }
}
