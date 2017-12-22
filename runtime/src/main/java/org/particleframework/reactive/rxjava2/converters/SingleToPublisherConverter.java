/*
 * Copyright 2017 original authors
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
package org.particleframework.reactive.rxjava2.converters;

import io.reactivex.Single;
import org.particleframework.core.convert.ConversionContext;
import org.particleframework.core.convert.TypeConverter;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Optional;

/**
 * Converts a {@link Single} to a {@link Publisher}
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
public class SingleToPublisherConverter implements TypeConverter<Single, Publisher> {
    @Override
    public Optional<Publisher> convert(Single object, Class<Publisher> targetType, ConversionContext context) {
        return Optional.of(
                object.toFlowable()
        );
    }
}