/*
 * Copyright 2017-2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.health;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.scheduling.annotation.Scheduled;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A scheduled task that sends a periodic heartbeat whilst the server is active.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Requires(property = "micronaut.heartbeat.enabled", value = "true", defaultValue = "true")
@Requires(property = ApplicationConfiguration.APPLICATION_NAME)
@Requires(beans = EmbeddedServer.class)
@Requires(notEnv = Environment.ANDROID)
public class HeartbeatTask implements ApplicationEventListener<ServiceStartedEvent> {

    private AtomicReference<ServiceInstance> eventReference = new AtomicReference<>();

    private final ApplicationEventPublisher eventPublisher;
    private final HeartbeatConfiguration configuration;
    private final CurrentHealthStatus currentHealthStatus;

    /**
     * @param eventPublisher      To publish the events related to heartbeat
     * @param configuration       The configurations for heartbeat
     * @param currentHealthStatus The current status of health indicator
     */
    public HeartbeatTask(ApplicationEventPublisher eventPublisher, HeartbeatConfiguration configuration, CurrentHealthStatus currentHealthStatus) {
        this.eventPublisher = eventPublisher;
        this.configuration = configuration;
        this.currentHealthStatus = currentHealthStatus;
    }

    /**
     * Publish the heartbeat event with current health status.
     */
    @Scheduled(fixedDelay = "${micronaut.heartbeat.interval:15s}", initialDelay = "${micronaut.heartbeat.initial-delay:5s}")
    public void pulsate() {
        ServiceInstance instance = eventReference.get();
        if (instance != null) {
            eventPublisher.publishEvent(new HeartbeatEvent(instance, currentHealthStatus.current()));
        }
    }

    @Override
    public void onApplicationEvent(ServiceStartedEvent event) {
        eventReference.set(event.getSource());
    }
}
