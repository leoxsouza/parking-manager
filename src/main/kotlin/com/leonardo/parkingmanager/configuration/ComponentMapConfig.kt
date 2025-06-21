package com.leonardo.parkingmanager.configuration

import dev.krud.spring.componentmap.ComponentMapPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ComponentMapConfig {
    @Bean
    fun componentMapPostProcessor(): ComponentMapPostProcessor {
        return ComponentMapPostProcessor()
    }
}