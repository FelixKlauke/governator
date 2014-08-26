package com.netflix.governator.guice.serviceloader;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;

public class ServiceLoaderTest {
    public static class BoundServiceImpl implements TestService {
        private Boolean injected;

        @Inject
        public BoundServiceImpl(Boolean injected) {
            this.injected = injected;
        }
        
        @Override
        public boolean isInjected() {
            return injected;
        }
    }
    
    @Test
    public void testMultibindings() {
        Injector injector = Guice.createInjector(new ServiceLoaderModule() {
            @Override
            protected void configureServices() {
                bind(Boolean.class).toInstance(true);
                Multibinder
                    .newSetBinder(binder(), TestService.class)
                    .addBinding()
                    .to(BoundServiceImpl.class);
                bindServices(TestService.class)
                    .usingMultibinding(true);
            }
        });
        
        Set<TestService> services = (Set<TestService>) injector.getInstance(Key.get(Types.setOf(TestService.class)));
        Assert.assertEquals(2, services.size());
        for (TestService service : services) {
            Assert.assertTrue(service.isInjected());
            System.out.println("   " + service.getClass().getName());
        }
    }
    
    @Test
    public void testNoMultibindings() {
        Injector injector = Guice.createInjector(new ServiceLoaderModule() {
            @Override
            protected void configureServices() {
                bind(Boolean.class).toInstance(true);
                bindServices(TestService.class);
            }
        });
        
        Set<TestService> services = (Set<TestService>) injector.getInstance(Key.get(Types.setOf(TestService.class)));
        Assert.assertEquals(1, services.size());
        for (TestService service : services) {
            Assert.assertTrue(service.isInjected());
            System.out.println("   " + service.getClass().getName());
        }
    }
}
