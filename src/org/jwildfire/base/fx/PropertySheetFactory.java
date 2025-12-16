package org.jwildfire.base.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Optional;

public class PropertySheetFactory {

    public static ObservableList<Item> createItems(Object bean) {
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) {
                    continue; // Skip the "class" property
                }
                // Only add properties that have both read and write methods (or at least read)
                // For a property sheet, we usually want editable ones, or read-only display.
                if (pd.getReadMethod() != null) {
                    list.add(new BeanPropertyItem(bean, pd));
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class BeanPropertyItem implements Item {
        private final Object bean;
        private final PropertyDescriptor pd;

        public BeanPropertyItem(Object bean, PropertyDescriptor pd) {
            this.bean = bean;
            this.pd = pd;
        }

        @Override
        public Class<?> getType() {
            return pd.getPropertyType();
        }

        @Override
        public String getCategory() {
            // JWildfire ManagedObjects might not have explicit categories in standard BeanInfo
            // unless we parse custom annotations or L2FProd extensions.
            // For now, return a default or group by something else if needed.
            // We could check for "expert" flag etc.
            return "General";
        }

        @Override
        public String getName() {
            return pd.getDisplayName();
        }

        @Override
        public String getDescription() {
            return pd.getShortDescription();
        }

        @Override
        public Object getValue() {
            try {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    return readMethod.invoke(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setValue(Object value) {
            try {
                Method writeMethod = pd.getWriteMethod();
                if (writeMethod != null) {
                    writeMethod.invoke(bean, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            // Java Beans don't inherently provide JavaFX Observables.
            // We return empty, which means the PropertySheet won't automatically sync
            // external changes unless we wrap the bean in a JavaFX BeanAdapter, 
            // but for a simple property editor, this is often sufficient.
            return Optional.empty();
        }
    }
}
