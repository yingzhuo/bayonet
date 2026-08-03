package com.github.yingzhuo.bayonet.beandef;

import com.github.yingzhuo.bayonet.common.Logic;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * {@link ConditionalOnResource} 条件注解的求值实现。
 *
 * @author 应卓
 * @since 4.1.1
 */
class ConditionalOnResourceCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var missing = new ArrayList<String>();
        var matching = new ArrayList<String>();
        var loader = context.getResourceLoader();
        var environment = context.getEnvironment();

        var attributes = BeanRegistrarHelper.getRequiredAnnotationAttributes(metadata, ConditionalOnResource.class);

        var logic = attributes.<Logic>getEnum("logic");
        var locations = new ArrayList<>(Arrays.asList(attributes.getStringArray("resources")));

        Assert.state(!locations.isEmpty(),
                "@ConditionalOnResource annotations must specify at least one resource location");

        for (var location : locations) {
            var resource = environment.resolvePlaceholders(location);
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
}
