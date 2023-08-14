package com.eus.initialize;

import com.eus.service.KeyGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyGeneratorInitializer implements SmartInitializingSingleton {

    private final KeyGeneratorService keyGeneratorService;

    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("Initializer running...");
        keyGeneratorService.generateKeyOnQueueBeingUnderThreshold();
        System.out.println("Initializer completed...");
    }
}
