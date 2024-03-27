package com.holland.spring.boot.frame.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.holland.infrastructure.kit.kit.DateKit;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * 前后端交互序列化反序列化配置
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class Jackson2ObjectMapperConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateKit.DTF_YYYY_MM_DD_HH_MM_SS));
            jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateKit.DTF_YYYY_MM_DD_HH_MM_SS));

            jacksonObjectMapperBuilder.serializerByType(LocalDate.class, new LocalDateSerializer(DateKit.DTF_YYYY_MM_DD));
            jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateKit.DTF_YYYY_MM_DD));

            jacksonObjectMapperBuilder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateKit.DTF_HH_MM_SS));
            jacksonObjectMapperBuilder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateKit.DTF_HH_MM_SS));

            jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));

            // 前端精度16位，long返回值需要转字符串
            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);

            // 数字型把末尾多余的0去掉
            jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, new MyBigDecimalJsonSerializer());

            // 方便前端入参传true、false
            jacksonObjectMapperBuilder.deserializerByType(Integer.class, new MyIntegerJsonDeserializer(Integer.class, null));
            jacksonObjectMapperBuilder.deserializerByType(Integer.TYPE, new MyIntegerJsonDeserializer(Integer.TYPE, 0));
        };
    }

    public static class MyIntegerJsonDeserializer extends JsonDeserializer<Integer> {
        /**
         * 代码原本的 IntegerDeserializer
         */
        private final NumberDeserializers.IntegerDeserializer originIntegerDeserializer;

        public MyIntegerJsonDeserializer(Class<Integer> cls, Integer nvl) {
            originIntegerDeserializer = new NumberDeserializers.IntegerDeserializer(cls, nvl);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return 1;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return 0;
            }
            return originIntegerDeserializer.deserialize(p, ctx);
        }
    }

    public static class MyBigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                final String stringValue = value.stripTrailingZeros().toPlainString();
                gen.writeString(stringValue);
            } else {
                gen.writeNull();
            }
        }
    }
}
