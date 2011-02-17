/*
 * Copyright 2003-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.eclipse.dsl.pointcuts.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.eclipse.dsl.pointcuts.AbstractPointcut;
import org.codehaus.groovy.eclipse.dsl.pointcuts.BindingSet;
import org.codehaus.groovy.eclipse.dsl.pointcuts.GroovyDSLDContext;
import org.codehaus.groovy.eclipse.dsl.pointcuts.IPointcut;

/**
 * the match returns true if the pattern passed in has a field with the
 * supplied characteristics (either a name, or another pointcut such as hasAnnotation).
 * @author andrew
 * @created Feb 11, 2011
 */
public class FindFieldPointcut extends AbstractPointcut {

    public FindFieldPointcut(String containerIdentifier) {
        super(containerIdentifier);
    }

    public BindingSet matches(GroovyDSLDContext pattern) {
        List<FieldNode> fields = getFields(pattern);
        if (fields == null || fields.size() == 0) {
            return null;
        }
        
        Object first = getFirstArgument();
        if (first instanceof String) {
            List<FieldNode> matches = new ArrayList<FieldNode>();
            for (FieldNode field : fields) {
                if (field.getName().equals(first)) {
                    matches.add(field);
                }
            }
            if (matches.size() == 0) {
                return null;
            } else if (matches.size() == 1) {
                return new BindingSet(matches.get(0));
            } else {
                return new BindingSet(matches);
            }
        } else {
            pattern.setOuterPointcutBinding(fields);
            return ((IPointcut) first).matches(pattern);
        }
    }

    /**
     * extracts fields from the outer binding, or from the current type if there is no outer binding
     * the outer binding should be either a {@link Collection} or a {@link ClassNode}
     */
    private List<FieldNode> getFields(GroovyDSLDContext pattern) {
        Object outer = pattern.getOuterPointcutBinding();
        if (outer == null) {
            return pattern.getCurrentType().getFields();
        } else {
            if (outer instanceof Collection<?>) {
                List<FieldNode> fields = new ArrayList<FieldNode>();
                for (Object elt : (Collection<Object>) outer) {
                    if (elt instanceof FieldNode) {
                        fields.add((FieldNode) elt);
                    }
                }
                return fields;
            } else if (outer instanceof ClassNode) {
                return ((ClassNode) outer).getFields();
            }
        }
        return null;
    }

    /**
     * Expecting one argument that can either be a string or another pointcut
     */
    @Override
    public String verify() {
        String oneStringOrOnePointcutArg = oneStringOrOnePointcutArg();
        if (oneStringOrOnePointcutArg == null) {
            return super.verify();
        }
        return oneStringOrOnePointcutArg;
    }
}
