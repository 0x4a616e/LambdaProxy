package de.jangassen.lambda.yaml;
// Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT

/*
The MIT License (MIT)

Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Allows snakeyaml to parse YAML templates that contain short forms of
 * CloudFormation intrinsic functions.
 *
 * @author Trek10, Inc.
 */
public class IntrinsicsYamlConstructor extends Constructor {
    public IntrinsicsYamlConstructor(Class<?> clazz) {
        super(clazz);

        addIntrinsic("And");
        addIntrinsic("Base64");
        addIntrinsic("Cidr");
        addIntrinsic("Condition", false);
        addIntrinsic("Equals");
        addIntrinsic("FindInMap");
        addIntrinsic("GetAtt", true, true);
        addIntrinsic("GetAZs");
        addIntrinsic("If");
        addIntrinsic("ImportValue");
        addIntrinsic("Join");
        addIntrinsic("Not");
        addIntrinsic("Or");
        addIntrinsic("Ref", false);
        addIntrinsic("Select");
        addIntrinsic("Split");
        addIntrinsic("Sub");
    }

    private void addIntrinsic(String tag) {
        addIntrinsic(tag, true);
    }

    private void addIntrinsic(String tag, boolean attachFnPrefix) {
        addIntrinsic(tag, attachFnPrefix, false);
    }

    private void addIntrinsic(String tag, boolean attachFnPrefix, boolean forceSequenceValue) {
        this.yamlConstructors.put(new Tag("!" + tag), getConstructFunction(attachFnPrefix, forceSequenceValue));
    }

    protected ConstructFunction getConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
        return new ConstructFunction(attachFnPrefix, forceSequenceValue);
    }

    protected class ConstructFunction extends AbstractConstruct {
        protected final boolean attachFnPrefix;
        protected final boolean forceSequenceValue;

        public ConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
            this.attachFnPrefix = attachFnPrefix;
            this.forceSequenceValue = forceSequenceValue;
        }

        public Object construct(Node node) {
            String key = node.getTag().getValue().substring(1);
            String prefix = attachFnPrefix ? "Fn::" : "";
            Map<String, Object> result = new HashMap<>();

            result.put(prefix + key, constructIntrinsicValueObject(node));
            return result;
        }

        protected Object constructIntrinsicValueObject(Node node) {
            if (node instanceof ScalarNode) {
                Object val = constructScalar((ScalarNode) node);
                if (forceSequenceValue) {
                    val = Arrays.asList(((String) val).split("\\."));
                }
                return val;
            } else if (node instanceof SequenceNode) {
                return constructSequence((SequenceNode) node);
            } else if (node instanceof MappingNode) {
                return constructMapping((MappingNode) node);
            }
            throw new YAMLException("Intrisic function arguments cannot be parsed.");
        }
    }
}