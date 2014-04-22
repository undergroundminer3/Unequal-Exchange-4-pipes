package me.undergroundminer3.uee4.util2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import sun.reflect.ConstructorAccessor;

public class EnumHack {


	public static Object newInstance(final Constructor c, final Object ... args) throws Exception {
		
		Object result = null;
		try {
			Method[] methods = Constructor.class.getDeclaredMethods();
			Field[] fields = Constructor.class.getDeclaredFields();
			ConstructorAccessor ctorc = null;
			
			for (int f = 0; f < fields.length; f++) {
				final Field currentField = fields[f];
				
				if (currentField.getName().equals("constructorAccessor")) {
					currentField.setAccessible(true);
					ctorc = (ConstructorAccessor) currentField.get(c);
				}
			}
			
			Method accessMethod = null;
			

			if (ctorc == null) {
				for (int m = 0; m < methods.length; m++) {
					final Method currentMethod = methods[m];
					
					if (currentMethod.getName().equals("acquireConstructorAccessor")) {
						accessMethod = currentMethod;
					}
				}
				accessMethod.setAccessible(true);
				ctorc = (ConstructorAccessor) accessMethod.invoke(c, new Object[] {});
			}
			
			Type[] params = c.getGenericParameterTypes();
			
			for (int p = 0; p < params.length; p++) {
				System.out.println(params[p].getTypeName());
			}
			
			result = ctorc.newInstance(args);
		} catch (final Exception e) {
			throw e;
		}
		return result;
	}
}
