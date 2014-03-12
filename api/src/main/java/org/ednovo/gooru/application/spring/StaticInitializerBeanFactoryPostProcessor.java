/////////////////////////////////////////////////////////////
// StaticInitializerBeanFactoryPostProcessor.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.application.spring;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class StaticInitializerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private Map classes;
    private BeanWrapperImpl bri;

	public StaticInitializerBeanFactoryPostProcessor() {
        bri = new BeanWrapperImpl();
	}

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for (Iterator classIterator = classes.keySet().iterator(); classIterator.hasNext(); ) {
            String className = (String)classIterator.next();
            //System.out.println("Class " + className + ":");
            Map vars = (Map)classes.get(className);
            Class c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new StaticInitializerBeansException("Class not found for " + className, e);
            }
            Method[] methods = c.getMethods();
            for (Iterator fieldIterator = vars.keySet().iterator(); fieldIterator.hasNext(); ) {
                String fieldName = (String)fieldIterator.next();
                Object value = vars.get(fieldName);
                Method method = findStaticSetter(methods, fieldName);
                if (method == null) {
                    throw new StaticInitializerBeansException("No static setter method found for class " +
                            className + ", field " + fieldName);
                }
                //System.out.println("\tFound method " + method.getName() + " for field " + fieldName + ", value " + value);
                Object newValue = bri.convertIfNecessary(value, getPropertyType(method));
                try {
                    method.invoke(null, new Object[] {newValue});
                } catch (Exception e) {
                    throw new StaticInitializerBeansException("Invocation of method " + method.getName() +
                            " on class " + className + " with value " + value + " failed.", e);
                }
            }
        }
    }

    private Class getPropertyType(Method setter) {
        Class params[] = setter.getParameterTypes();
        if (params.length != 1) {
            throw new StaticInitializerBeansException("bad write method arg count: " + setter);
        }
        return  params[0];
    }

    /**
     * Look for a static setter method for field named fieldName in Method[].
     * Return null if none found.
     * @param methods
     * @param fieldName
     * @return
     */
    private Method findStaticSetter(Method[] methods, String fieldName) {
        String methodName = setterName(fieldName);
        for (int i=0; i<methods.length; i++) {
            if (methods[i].getName().equals(methodName) &&
                                                Modifier.isStatic(methods[i].getModifiers())) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * return the standard setter name for field fieldName
     * @param fieldName
     * @return
     */
    private String setterName(String fieldName) {
        String nameToUse = null;
        if (fieldName.length() == 1) {
            if (Character.isLowerCase(fieldName.charAt(0))) {
                nameToUse = fieldName.toUpperCase();
            } else {
                nameToUse = fieldName;
            }
        } else {
            if (Character.isLowerCase(fieldName.charAt(0)) && Character.isLowerCase(fieldName.charAt(1))) {
                nameToUse = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
            }  else {
                nameToUse = fieldName;
            }
        }
        return "set" + nameToUse;
    }

    public void setClasses(Map classes) {
        this.classes = classes;
    }
}

class StaticInitializerBeansException extends BeansException {
     StaticInitializerBeansException(String msg) {
         super(msg);
     }
     StaticInitializerBeansException(String msg, Throwable e) {
         super(msg, e);
     }
}
