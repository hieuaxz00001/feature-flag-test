package com.example.demo2;

import org.ff4j.FF4j;
import org.ff4j.audit.repository.EventRepository;
import org.ff4j.audit.repository.InMemoryEventRepository;
import org.ff4j.conf.FF4jConfiguration;
import org.ff4j.core.FeatureStore;
import org.ff4j.parser.yaml.YamlParser;
import org.ff4j.property.store.InMemoryPropertyStore;
import org.ff4j.property.store.PropertyStore;
import org.ff4j.store.InMemoryFeatureStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FF4jConfig {
    @Bean
    public FF4j ff4j() {
        /*
         * 0. Create bean needed for your stores based on the technology you want to use
         *
         * Because I pick in memory store, I want to load init values from a file I can use
         * YAML, XML or properties File. This is optional you can start with empty store
         * or even create feature programmatically.
         */
        InputStream dataSet = FF4j.class.getClassLoader().getResourceAsStream("ff4j-init-dataset.yml");
        // We imported ff4j-config-yaml to have this
        FF4jConfiguration initConfig = new YamlParser().parseConfigurationFile(dataSet);

        // 1. Define the store you want for Feature, Properties, Audit among 20 tech
        FeatureStore featureStore  = new InMemoryFeatureStore(initConfig);
        PropertyStore propertyStore = new InMemoryPropertyStore(initConfig);
        EventRepository logsAudit   = new InMemoryEventRepository();

        // 2. Build FF4j
        FF4j ff4jBean = new FF4j();
        ff4jBean.setPropertiesStore(propertyStore);
        ff4jBean.setFeatureStore(featureStore);
        ff4jBean.setEventRepository(logsAudit);

        // 3. Complete setup
        ff4jBean.setEnableAudit(true);
        ff4jBean.setAutocreate(true);
        return ff4jBean;

        // you can do the same in 1 line
        //return new FF4j(new YamlParser(), "ff4j-init-dataset.yml").audit(true);;
    }
}