/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.buildoption;

import org.gradle.cli.CommandLineParser;
import org.gradle.cli.ParsedCommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A build option that takes a list value e.g. {@code "-Iinit1.gradle -Iinit2.gradle"}.
 *
 * @since 4.3
 */
public abstract class ListBuildOption<T> extends AbstractBuildOption<T> {

    public ListBuildOption(String gradleProperty) {
        super(gradleProperty);
    }

    public ListBuildOption(String gradleProperty, CommandLineOptionConfiguration... commandLineOptionConfigurations) {
        super(gradleProperty, commandLineOptionConfigurations);
    }

    @Override
    public void applyFromProperty(Map<String, String> properties, T settings) {
        String value = properties.get(gradleProperty);

        if (value != null) {
            String[] splitValues = value.split("\\s*,\\s*");
            applyTo(Arrays.asList(splitValues), settings, Origin.GRADLE_PROPERTY);
        }
    }

    @Override
    public void configure(CommandLineParser parser) {
        for (CommandLineOptionConfiguration config : commandLineOptionConfigurations) {
            configureCommandLineOption(parser, config.getAllOptions(), config.getDescription(), config.getDeprecationWarning(), config.isIncubating()).hasArguments();
        }
    }

    @Override
    public void applyFromCommandLine(ParsedCommandLine options, T settings) {
        for (CommandLineOptionConfiguration config : commandLineOptionConfigurations) {
            if (options.hasOption(config.getLongOption())) {
                List<String> value = options.option(config.getLongOption()).getValues();
                applyTo(value, settings, Origin.COMMAND_LINE);
            }
        }
    }

    public abstract void applyTo(List<String> values, T settings, Origin origin);
}
