package com.simra.konsumgandalf.common.utils.converter;

import com.opencsv.bean.AbstractBeanField;
import com.simra.konsumgandalf.common.models.interfaces.EnumTranslatable;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * Basic Converter for Enums which translates int like (f.e. "1") values to Enum values.
 */
@NoArgsConstructor
public class EnumConverter<T extends Enum<T> & EnumTranslatable> extends AbstractBeanField<T, String> {

	private Class<T> enumType;

	@Override
	protected T convert(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		for (T type : enumType.getEnumConstants()) {
			if (type.getValue() == Integer.parseInt(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid type value: " + value);
	}

	@Override
	public void setField(Field field) {
		super.setField(field);
		this.enumType = (Class<T>) field.getType();
	}

}
