package org.sapphon.personal.upwise;

import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;

import javax.persistence.GeneratedValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
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

	public static <T extends Annotation> T assertConstructorHasAnnotationOfTypeAndGet(Class<?> c, List<Class> argumentClasses, int argumentIndex, Class<T> expectedAnnotationClass){
		T foundAnnotation = null;
		try{
			final Constructor<?> targetedConstructor = c.getConstructor(argumentClasses.stream().toArray(Class[]::new));
			final Annotation[] parameterAnnotationMatrix = targetedConstructor.getParameterAnnotations()[argumentIndex];
			for (Annotation annotation : parameterAnnotationMatrix){
				if(annotation.annotationType().equals(expectedAnnotationClass)){
					foundAnnotation = (T) annotation;
				}
			}

		}
		catch(NoSuchMethodException exception){
		}
		assertNotNull(foundAnnotation);
		return foundAnnotation;
	}

	public static <T extends Annotation> T assertFieldHasAnnotationOfTypeAndGet(Class c, String fieldName, Class<T> annotationClass) {
		T foundAnnotation = getFieldAnnotation(c, fieldName, annotationClass);
		assertNotNull(foundAnnotation);
		return foundAnnotation;
	}

	public static void assertTimestampBetweenInclusive(Timestamp toMeasure, Timestamp before, Timestamp after){
		assertTrue("Timestamp not within acceptable range", toMeasure.compareTo(before) >= 0 && toMeasure.compareTo(after) <= 0);
	}

	public static void assertAnnotatedPropertyAndDefault(String propertyString, String expectedPropertyName, String expectedPropertyDefault) {
        assertEquals("${"+expectedPropertyName+":"+expectedPropertyDefault+"}", propertyString);
    }
}
