package com.example.demo2.schedule;

import com.example.demo2.config.FF4jConfig;
import com.example.demo2.config.TcbFF4jConfiguration;
import com.example.demo2.constant.CustomPropertiesEnum;
import jakarta.annotation.PostConstruct;
import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


@Component
@EnableScheduling
@ConditionalOnProperty(value = "ff4j.job.enable-auto-reload-job", havingValue = "true", matchIfMissing = true)
@DependsOn("featureFlagConfig")
public class FeatureFlagAutoReload {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagAutoReload.class);
    private final TcbFF4jConfiguration tcbFF4jConfiguration;
    private final FF4j ff4j;

    @PostConstruct
    void init() {
        log.info("Init FeatureFlagAutoReload");
    }

    public FeatureFlagAutoReload(TcbFF4jConfiguration tcbFF4jConfiguration, FF4j ff4j) {
        this.tcbFF4jConfiguration = tcbFF4jConfiguration;
        this.ff4j = ff4j;
    }

    @Scheduled(cron  = "${ff4j.job.time}") // Reload every minute (60000 ms)
    public void reloadFeatures() {
        log.info("Reloading features... Old: {}", ff4j.getFeatures());
        ff4j.getFeatures()
                .entrySet()
                .stream()
                .filter(entry -> Optional.ofNullable(entry)
                        .map(Map.Entry::getValue)
                        .map(Feature::getCustomProperties)
                        .map(customPros -> customPros.get(CustomPropertiesEnum.autoReload.name()).asBoolean())
                        .orElse(Boolean.FALSE))
                .map(Map.Entry::getValue)
                .forEach(feature -> ff4j.delete(feature.getUid()));

        log.info("Reloading features...: {}", ff4j.getFeatures());

        tcbFF4jConfiguration.getFeatures()
                .entrySet()
                .stream()
                .filter(entry -> Optional.ofNullable(entry)
                        .map(Map.Entry::getValue)
                        .map(TcbFF4jConfiguration.TcbFeature::getCustomProperties)
                        .map(customPros -> customPros.get(CustomPropertiesEnum.autoReload.name()).asBoolean())
                        .orElse(Boolean.FALSE))
                .map(Map.Entry::getValue)
                .forEach( tcbFeature -> ff4j.createFeature(FF4jConfig.createFeature(tcbFeature)));
        log.info("Features reloaded... New: {}", ff4j.getFeatures());
    }
}
