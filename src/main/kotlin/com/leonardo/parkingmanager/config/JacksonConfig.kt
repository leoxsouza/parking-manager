package com.leonardo.parkingmanager.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule

@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        
        // Registrar módulo Kotlin
        objectMapper.registerKotlinModule()
        
        // Configurar JavaTimeModule com nosso deserializador personalizado
        val javaTimeModule = JavaTimeModule()
        
        // Substituir o deserializador padrão de Instant pelo nosso personalizado
        val instantModule = SimpleModule()
        instantModule.addDeserializer(Instant::class.java, CustomInstantDeserializer())
        
        objectMapper.registerModule(javaTimeModule)
        objectMapper.registerModule(instantModule)
        
        // Configurações adicionais
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        
        return objectMapper
    }
    
    /**
     * Deserializador personalizado para Instant que aceita formatos de data sem milissegundos ou zona horária
     */
    class CustomInstantDeserializer : JsonDeserializer<Instant>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
            val dateString = p.text
            
            return try {
                // Primeiro tenta o formato padrão do Instant
                Instant.parse(dateString)
            } catch (e: Exception) {
                try {
                    // Se falhar, tenta o formato sem milissegundos e zona horária
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    LocalDateTime.parse(dateString, formatter).toInstant(ZoneOffset.UTC)
                } catch (e2: Exception) {
                    // Se ainda falhar, tenta outros formatos comuns
                    try {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        LocalDateTime.parse(dateString, formatter).toInstant(ZoneOffset.UTC)
                    } catch (e3: Exception) {
                        throw e3
                    }
                }
            }
        }
    }
}
