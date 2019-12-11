package me.izhong.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.OrderComparator;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class PropertyUtil {

    @Autowired
    private List<PropertySourcesPlaceholderConfigurer> configurers;
    private List<PropertySourcesPropertyResolver> resolvers;

    @PostConstruct
    private void init() {
        OrderComparator.sort(configurers);
        resolvers = new ArrayList<PropertySourcesPropertyResolver>(configurers.size());
        for (PropertySourcesPlaceholderConfigurer configurer : configurers) {
            resolvers.add(new PropertySourcesPropertyResolver(configurer.getAppliedPropertySources()));
        }
    }

    public String getProperty(String key) {
        for (PropertySourcesPropertyResolver resolver : resolvers) {
            if (resolver.containsProperty(key)) {
                return resolver.getProperty(key);
            }
        }
        return null;
    }

}
