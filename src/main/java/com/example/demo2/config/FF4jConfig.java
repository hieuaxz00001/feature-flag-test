package com.example.demo2.config;

import org.ff4j.FF4j;
import org.ff4j.cache.FF4jCacheManagerRedis;
import org.ff4j.core.Feature;
import org.ff4j.core.FeatureStore;
import org.ff4j.property.Property;
import org.ff4j.property.store.InMemoryPropertyStore;
import org.ff4j.property.store.PropertyStore;
import org.ff4j.redis.RedisConnection;
import org.ff4j.redis.RedisKeysBuilder;
import org.ff4j.store.FeatureStoreRedis;
import org.ff4j.store.InMemoryFeatureStore;
import org.ff4j.store.PropertyStoreRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FF4jConfig {
    private static final Logger log = LoggerFactory.getLogger(FF4jConfig.class);


    private final TcbFF4jConfiguration tcbFF4jConfiguration;

    public FF4jConfig(TcbFF4jConfiguration tcbFF4jConfiguration) {
        this.tcbFF4jConfiguration = tcbFF4jConfiguration;
    }

    public static Feature createFeature(TcbFF4jConfiguration.TcbFeature tcbFeature) {
        Feature feature = new Feature(tcbFeature.getUid());
        feature.setEnable(tcbFeature.isEnable());
        feature.setDescription(tcbFeature.getDescription());
        feature.setGroup(tcbFeature.getGroup());
        feature.setPermissions(tcbFeature.getPermissions());
        feature.setFlippingStrategy(tcbFeature.getFlippingStrategy());
        Map<String, Property<?>> properties = new HashMap<>(tcbFeature.getCustomProperties());
        feature.setCustomProperties(properties);
        return feature;
    }

    @Bean
    public FF4j featureFlagConfig() {
        log.info("Init feature flag - featureStore: {}", tcbFF4jConfiguration.getFeatureStore());
        switch (tcbFF4jConfiguration.getFeatureStore()){
            case REDIS -> {
                return initFeatureFlagStoreInRedis();
            }
            case DATABASE -> {
                return initFeatureFlagStoreInDatabase();
            }
            default -> {
                return initFeatureFlagStoreInMemory();
            }
        }
    }

    private FF4j initFeatureFlagStoreInRedis(){
        log.info("Init feature flag - featureStore: Redis");
        RedisKeysBuilder redisKeysBuilder = new RedisKeysBuilder();
        RedisConnection redisConnection = new RedisConnection();
        FF4jCacheManagerRedis ff4jCacheManagerRedis = new FF4jCacheManagerRedis(redisConnection, redisKeysBuilder);
        FeatureStoreRedis featureStore = new FeatureStoreRedis(redisConnection, redisKeysBuilder);
        PropertyStoreRedis propertyStore = new PropertyStoreRedis(redisConnection, redisKeysBuilder);

        FF4j ff4j = new FF4j();

        ff4j.setPropertiesStore(propertyStore);
        ff4j.setFeatureStore(featureStore);
        ff4j.cache(ff4jCacheManagerRedis);

        // Initialize your features
        ff4j.createFeature("myFeature", true);
        ff4j.createFeature("anotherFeature", false);

        return ff4j;

    }

    private FF4j initFeatureFlagStoreInDatabase(){
        log.info("Init feature flag - featureStore: Database");
        FF4j ff4j = new FF4j();
        // to do
        return ff4j;
    }

    private FF4j initFeatureFlagStoreInMemory() {
        log.info("Init feature flag - featureStore: Memory");
        log.info("{}", tcbFF4jConfiguration.getFeatures().get("myFeature").getCustomProperties());
        FF4j ff4jBean = new FF4j();

        // 1. Define the store you want for Feature, Properties, Audit among 20 tech
        FeatureStore featureStore = new InMemoryFeatureStore();
        PropertyStore propertyStore = new InMemoryPropertyStore();

        // 2. Build FF4j
        ff4jBean.setPropertiesStore(propertyStore);
        ff4jBean.setFeatureStore(featureStore);

        // 3. Complete setup
        tcbFF4jConfiguration.getFeatures()
                .forEach((featureName, tcbFeature) -> ff4jBean.createFeature(createFeature(tcbFeature)));
        ff4jBean.setAutocreate(tcbFF4jConfiguration.isAutoCreate());
        return ff4jBean;
    }
//private final Environment env;
//
//    public FF4jConfig(Environment env) {
//        this.env = env;
//    }
//
//    @Bean
//    public FF4j ff4j() {
//        FF4j ff4j = new FF4j();
//        env.getProperty("ff4j");
//        return ff4j;
//    }
}