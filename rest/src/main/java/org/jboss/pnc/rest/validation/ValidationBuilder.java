/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package org.jboss.pnc.rest.validation;

import org.jboss.pnc.model.GenericEntity;
import org.jboss.pnc.rest.validation.exceptions.ConflictedEntryException;
import org.jboss.pnc.rest.validation.exceptions.RepositoryViolationException;
import org.jboss.pnc.rest.validation.exceptions.InvalidEntityException;
import org.jboss.pnc.rest.validation.groups.ValidationGroup;
import org.jboss.pnc.spi.datastore.repositories.api.Predicate;
import org.jboss.pnc.spi.datastore.repositories.api.Repository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ValidationBuilder<T> {

    private Class<? extends ValidationGroup> validationGroup;
    private T objectToBeValidated;

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ValidationBuilder(Class<? extends ValidationGroup> validationGroup, T objectToBeValidated) {
        this.validationGroup = validationGroup;
        this.objectToBeValidated = objectToBeValidated;
    }

    public static ValidationBuilder validateObject(Object object, Class<? extends ValidationGroup> validationGroup) {
        if(validationGroup == ValidationGroup.class) {
            throw new IllegalArgumentException("Use validation subclasses");
        }

        return new ValidationBuilder(validationGroup, object);
    }

    public static ValidationBuilder validateObject(Class<? extends ValidationGroup> validationGroup) {
        return validateObject(null, validationGroup);
    }

    public ValidationBuilder validateAnnotations() throws InvalidEntityException {
        if(objectToBeValidated != null) {
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(objectToBeValidated, validationGroup);
            if(!constraintViolations.isEmpty()) {
                throw new InvalidEntityException(constraintViolations.iterator().next());
            }
        }
        return this;
    }

    public ValidationBuilder validateField(String property, Object value) throws InvalidEntityException {
        if(objectToBeValidated != null) {
            try {
                Field field = objectToBeValidated.getClass().getDeclaredField(property);
                field.setAccessible(true);
                Object valueFromObject = field.get(objectToBeValidated);
                if((value == null && valueFromObject == null) || (value != null && value.equals(valueFromObject)))
                    return this;
                throw new InvalidEntityException(field);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("No such field", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not access field", e);
            }
        }
        return this;
    }

    public ValidationBuilder validateConflict(ConflictedEntryValidator validate) throws
            ConflictedEntryException {
        ConflictedEntryValidator.ConflictedEntryValidationError validationError = validate.validate();
        if(validationError != null) {
            throw new ConflictedEntryException(validationError.getMessage(), validationError.getConflictedEntity(), validationError.getConflictedRecordId());
        }
        return this;
    }

    public <DBEntity extends GenericEntity<ID>, ID extends Number> ValidationBuilder validateAgainstRepository(Repository<DBEntity, ID> repository, Map<String, Predicate<DBEntity>> predicates, boolean shouldExist)
            throws RepositoryViolationException {
        for(Map.Entry<String, Predicate<DBEntity>> entry : predicates.entrySet()) {
            int count = repository.count(entry.getValue());
            if((!shouldExist && count != 0) || (shouldExist && count == 0)) {
                throw new RepositoryViolationException(entry.getKey());
            }
        }
        return this;
    }

    public <DBEntity extends GenericEntity<ID>, ID extends Number> ValidationBuilder validateAgainstRepository(Repository<DBEntity, ID> repository, ID id, boolean shouldExist)
            throws RepositoryViolationException {
        DBEntity dbEntity = repository.queryById(id);
        if((!shouldExist && dbEntity != null) || (shouldExist && dbEntity == null)) {
            StringBuilder sb = new StringBuilder("Entity should ");
            if(!shouldExist) {
                sb.append(" not ");
            }
            sb.append("exist in the DB");
            throw new RepositoryViolationException(sb.toString());
        }
        return this;
    }

    public ValidationBuilder validateCondition(boolean condition, String message)
            throws InvalidEntityException {
        if(!condition) {
            throw new InvalidEntityException(message);
        }
        return this;
    }

}