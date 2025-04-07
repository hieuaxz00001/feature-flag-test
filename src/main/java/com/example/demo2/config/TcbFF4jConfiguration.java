package com.example.demo2.config;

import com.example.demo2.constant.FeatureStoreEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ff4j.core.FlippingStrategy;
import org.ff4j.property.PropertyString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties( prefix = "ff4j")
@AllArgsConstructor
@NoArgsConstructor
@RefreshScope
public class TcbFF4jConfiguration implements Serializable {
    private FeatureStoreEnum featureStore;
    private boolean autoCreate = false;
    private boolean audit = false;
    private Map<String, TcbFeature> features = new HashMap<>();

    @Getter
    @Setter
    public static class TcbFeature implements Serializable{
        @Serial
        private static final long serialVersionUID = 4987351300418126366L;
        private String uid;
        private boolean enable;
        private String description;
        private String group;
        private Set<String> permissions;
        private FlippingStrategy flippingStrategy;
        private Map<String, PropertyString> customProperties;

    }
}
