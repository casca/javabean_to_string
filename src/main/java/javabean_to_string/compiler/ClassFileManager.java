package javabean_to_string.compiler;

import java.io.IOException;
import java.security.SecureClassLoader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class ClassFileManager extends ForwardingJavaFileManager {
	/**
	 * Instance of JavaClassObject that will store the compiled bytecode of our
	 * class
	 */
	private JavaClassObject jclassObject;

	/**
	 * Will initialize the manager with the specified standard java file manager
	 * 
	 * @param standardManger
	 */
	public ClassFileManager(final StandardJavaFileManager standardManager) {
		super(standardManager);
	}

	/**
	 * Will be used by us to get the class loader for our compiled class. It
	 * creates an anonymous class extending the SecureClassLoader which uses the
	 * byte code created by the compiler and stored in the JavaClassObject, and
	 * returns the Class for it
	 */
	@Override
	public ClassLoader getClassLoader(final Location location) {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(final String name)
					throws ClassNotFoundException {
				final byte[] b = jclassObject.getBytes();
				return super.defineClass(name, jclassObject.getBytes(), 0,
						b.length);
			}
		};
	}

	/**
	 * Gives the compiler an instance of the JavaClassObject so that the
	 * compiler can write the byte code into it.
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(final Location location,
			final String className, final Kind kind, final FileObject sibling)
			throws IOException {
		jclassObject = new JavaClassObject(className, kind);
		return jclassObject;
	}

}
