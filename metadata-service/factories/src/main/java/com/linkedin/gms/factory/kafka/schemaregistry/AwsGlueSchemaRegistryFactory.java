package com.linkedin.gms.factory.kafka.schemaregistry;

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;
import com.linkedin.gms.factory.config.ConfigurationProvider;
import com.linkedin.gms.factory.spring.YamlPropertySourceFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Slf4j
@Configuration
@PropertySource(value = "classpath:/application.yml", factory = YamlPropertySourceFactory.class)
@ConditionalOnProperty(name = "kafka.schemaRegistry.type", havingValue = AwsGlueSchemaRegistryFactory.TYPE)
public class AwsGlueSchemaRegistryFactory {

  public static final String TYPE = "AWS_GLUE";

  @Value("${kafka.schemaRegistry.awsGlue.region}")
  private String awsRegion;
  @Value("${kafka.schemaRegistry.awsGlue.registryName}")
  private Optional<String> registryName;

  @Bean
  @Nonnull
  protected SchemaRegistryConfig getInstance(ConfigurationProvider configurationProvider) {
    Map<String, Object> props = new HashMap<>();
    // FIXME: Properties for this factory should come from ConfigurationProvider object, specifically under the
    // KafkaConfiguration class. See InternalSchemaRegistryFactory as an example.
    props.put(AWSSchemaRegistryConstants.AWS_REGION, awsRegion);
    props.put(AWSSchemaRegistryConstants.DATA_FORMAT, "AVRO");
    props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, "true");
    props.put(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE, AvroRecordType.GENERIC_RECORD.getName());
    registryName.ifPresent(s -> props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, s));
    log.info("Creating AWS Glue registry");
    return new SchemaRegistryConfig(GlueSchemaRegistryKafkaSerializer.class, GlueSchemaRegistryKafkaDeserializer.class,
        props);
  }
}