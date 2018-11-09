package org.sapphon.personal.upwise;

import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;

import javax.persistence.GeneratedValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.Assert.*;

public class TestHelper {
	public static void assertListEquals(List list1, List list2){
		assertArrayEquals(list1.toArray(), list2.toArray());
	}

	public static <T extends Annotation> T getFieldAnnotation(

			Class<?> c, String fieldName, Class<T> annotation) {

		try {

			Field f = c.getDeclaredField(fieldName);

			return (T)f.getAnnotation(annotation);

		} catch (NoSuchFieldException nsfe) {

			throw new RuntimeException(nsfe);

		}

	}

	public static <T extends Annotation> T getClassAnnotation(

			Class<?> c, Class<T> annotation) {

		return (T) c.getAnnotation(annotation);

	}

	public static void assertAnnotationsPresentOnField(Class c, String fieldName, Class ... annotationsToCheckFor ){
		for (Class current : annotationsToCheckFor) {
			assertNotNull(getFieldAnnotation(c, fieldName, current));
		}
	}

	public static <T extends Annotation> T assertFieldHasAnnotationOfTypeAndGet(Class c, String fieldName, Class<T> annotationClass) {
		T foundAnnotation = getFieldAnnotation(c, fieldName, annotationClass);
		assertNotNull(foundAnnotation);
		return foundAnnotation;
	}
}
