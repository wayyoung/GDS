package gds;
/*-------------------------------------------------------------------------
 |   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
 |   RXTX is a native interface to serial ports in java.
 |   Copyright 1997-2012 by Trent Jarvi tjarvi@qbang.org and others who
 |   actually wrote it.  See individual source files for more information.
 |
 |   A copy of the LGPL v 2.1 may be found at
 |   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
 |   here for your convenience.
 |
 |   This library is free software; you can redistribute it and/or
 |   modify it under the terms of the GNU Lesser General Public
 |   License as published by the Free Software Foundation; either
 |   version 2.1 of the License, or (at your option) any later version.
 |
 |   This library is distributed in the hope that it will be useful,
 |   but WITHOUT ANY WARRANTY; without even the implied warranty of
 |   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 |   Lesser General Public License for more details.
 |
 |   An executable that contains no derivative of any portion of RXTX, but
 |   is designed to work with RXTX by being dynamically linked with it,
 |   is considered a "work that uses the Library" subject to the terms and
 |   conditions of the GNU Lesser General Public License.
 |
 |   The following has been added to the RXTX License to remove
 |   any confusion about linking to RXTX.   We want to allow in part what
 |   section 5, paragraph 2 of the LGPL does not permit in the special
 |   case of linking over a controlled interface.  The intent is to add a
 |   Java Specification Request or standards body defined interface in the
 |   future as another errorThrow but one is not currently available.
 |
 |   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
 |
 |   As a special errorThrow, the copyright holders of RXTX give you
 |   permission to link RXTX with independent modules that communicate with
 |   RXTX solely through the Sun Microsytems CommAPI interface version 2,
 |   regardless of the license terms of these independent modules, and to copy
 |   and distribute the resulting combined work under terms of your choice,
 |   provided that every copy of the combined work is accompanied by a complete
 |   copy of the source code of RXTX (the version of RXTX used to produce the
 |   combined work), being distributed under the terms of the GNU Lesser General
 |   Public License plus this errorThrow.  An independent module is a
 |   module which is not derived from or based on RXTX.
 |
 |   Note that people who make modified versions of RXTX are not obligated
 |   to grant this special errorThrow for their modified versions; it is
 |   their choice whether to do so.  The GNU Lesser General Public License
 |   gives permission to release a modified version without this errorThrow; this
 |   errorThrow also makes it possible to release a modified version which
 |   carries forward this errorThrow.
 |
 |   You should have received a copy of the GNU Lesser General Public
 |   License along with this library; if not, write to the Free
 |   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 |   All trademarks belong to their respective owners.
 --------------------------------------------------------------------------*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LibraryLoader enables driver implementations to load native libraries
 * based on the operating system and CPU architecture of the running system and
 * virtual machine.
 *
 * <p>
 * <b>This class is not intended to be used by API users. It is reserved for
 * driver implementors (service provider interface users).</b>
 * </p>
 *
 * Driver implementations typically use the Java Native Interface (JNI) to
 * access hardware or operating system features. To initialize a JNI based
 * driver the proper native library with respect to the operating system and CPU
 * architecture has to be loaded.
 *
 * @author Jeff Benjamin
 * @author Alexander Graf
 * @since TBD
 */
public final class LibraryLoader {

	public static boolean FFMPEG_DRIVER_LOADED = false;
	public static boolean FTDIXX_DRIVER_LOADED = false;
	public static final String OS_LINUX = "linux";
	public static final String OS_MACOSX = "osx";
	public static final String OS_WINDOWS = "windows";
	public static final String ARCH_X86 = "x86";
	public static final String ARCH_X86_64 = "x86_64";
	public static final Logger logger = LoggerFactory.getLogger(LibraryLoader.class.getName());
	public static String getOsClass() {
		return lr.osClass;
	}

	public static String getArchitecture() {
		return lr.architecture;
	}

	private ClassLoader classLoader;
	// private String resourcePath;
	private String osClass;
	private String architecture;

	private static LibraryLoader lr =new LibraryLoader();

	private LibraryLoader() {
		this.classLoader = LibraryLoader.class.getClassLoader();
		this.osClass = determineOsClass();
		this.architecture = determineArchitecture();
	}

	public static LibraryLoader instance(){
		return lr;
	}


	public static void loadFFmpegDriver() {

		if (FFMPEG_DRIVER_LOADED == true)
			return;
		try {
			if (!lr.loadLibrary("ffmpeg/impl", "avcodec"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "swscale"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "swresample"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "postproc"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "avfilter"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "avdevice"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "avformat"))
				throw new Exception();
			if (!lr.loadLibrary("ffmpeg/impl", "avutil"))
				throw new Exception();

			FFMPEG_DRIVER_LOADED = true;
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}

	}

	// public boolean loadLibrary(String resourceRoot, String nonHintedBaseName)
	// {
	// this.classLoader = classLoader;
	// this.osClass = determineOsClass();
	// this.architecture = determineArchitecture();
	// this.resourcePath = resourceRoot;
	//
	// return load(nonHintedBaseName, false);
	// }

	// /**
	// * Creates a new LibraryLoader. The given classLoader will be used to find
	// * the native library in the resources (e.g. a JAR file) at the given
	// * resourcePath.
	// *
	// * <p>e.g. if the libraries are packed into the directory bin/jni inside
	// * the JAR, then resourcePath is bin/jni.</p>
	// *
	// * @param classLoader the class loader used to find the bundled library
	// * @param resourcePath the path inside the JAR without trailing slash
	// */
	// public LibraryLoader(ClassLoader classLoader, String resourcePath) {
	// this.classLoader = classLoader;
	// this.resourcePath = resourcePath;
	// this.osClass = determineOsClass();
	// this.architecture = determineArchitecture();
	// }

	/**
	 * Tries to find and load the correct version of a given library.
	 *
	 * <p>
	 * To use a Java Native Interface library, a separate library must be
	 * available for each operating system and CPU architecture combination,
	 * which is supported. To make JNI work, the proper library for the current
	 * system is determined at runtime.
	 * </p>
	 *
	 * <p>
	 * This method finds the proper library using the following naming
	 * conventions:
	 * </p>
	 *
	 * <p>
	 * The <i>non hinted base name</i> is simply a (short) file base name. For
	 * example some library <i>Foo Bar Lib</i> might have a non hinted base name
	 * of <i>foobar</i>.
	 *
	 * <p>
	 * The <i>non hinted library name</i> is the mapped version of the non
	 * hinted base name. It was mapped to the operating system's naming
	 * convention for dynamic libraries using the System.mapLibraryName()
	 * method. This will typically assign a system dependent file extension. For
	 * example the non hinted library name of the <i>Foo Bar Lib</i> on Windows
	 * is <i>foobar.dll</i>. On linux it might look like <i>foobar.so</i>.
	 *
	 * The <i>hinted base name</i> starts with the non hinted base name. Then
	 * the operating system &lt;os&gt; and architecture &lt;arch&gt; hints
	 * follow, each separated by a dash (&#45; ASCII #45, U+002D). Therefor the
	 * <i>hinted base name</i> is formed as follows:
	 * </p>
	 *
	 * <p>
	 * &lt;libBaseName&gt;&#45;&lt;os&gt;&#45;&lt;arch&gt;
	 * </p>
	 *
	 * <p>
	 * The exact values for the &lt;os&gt; and &lt;arch&gt; fields are given in
	 * the following tables.
	 * </p>
	 *
	 * <table>
	 * <tr>
	 * <td><b>&lt;os&gt;</b></td>
	 * <td><b>operating system</b></td>
	 * </tr>
	 * <tr>
	 * <td>linux</td>
	 * <td>Linux</td>
	 * </tr>
	 * <tr>
	 * <td>windows</td>
	 * <td>Windows 95 to Windows 8</td>
	 * </tr>
	 * <tr>
	 * <td>osx</td>
	 * <td>Mac OS X</td>
	 * </tr>
	 * </table>
	 *
	 * <table>
	 * <tr>
	 * <td><b>&lt;arch&gt;</b></td>
	 * <td><b>architecture</b></td>
	 * </tr>
	 * <tr>
	 * <td>x86</td>
	 * <td>x86, i386 - i686</td>
	 * </tr>
	 * <tr>
	 * <td>x86_64</td>
	 * <td>x86 with 64 bit extension (amd64)</td>
	 * </tr>
	 * </table>
	 *
	 * <p>
	 * Please note: The target architecture of the library must fit the
	 * architecture of the used Java Virtual Machine and NOT the architecture
	 * used by the operating system. For example the hinted base name of the
	 * <i>Foo Bar Lib</i> on a 32 bit (x86) JVM running on a 64 bit (x86_64)
	 * Windows is <i>foobar-windows-x86</i>
	 * </p>
	 *
	 * <p>
	 * The <i>hinted library name</i> is the mapped version of the hinted base
	 * name. It was mapped to the operating system's naming convention for
	 * dynamic libraries using the System.mapLibraryName() method. This will
	 * typically assign a system dependent file extension. For example the
	 * hinted library name of the <i>Foo Bar Lib</i> on Windows x86 is
	 * <i>foobar-windows-x86.dll</i>. On linux it might look like
	 * <i>foobar-linux-x86.so</i>.
	 *
	 * <p>
	 * This method tries to find the library from the following locations in the
	 * given order:
	 * <ul>
	 * <li>Using the resources of the class loader (e.g. JAR file) with the
	 * hinted library name.</li>
	 * <li>On the java library path with the hinted library name.</li>
	 * <li>On the java library path with the non-hinted library name.</li>
	 * </ul>
	 * If the library was found in one location, the search is stopped without
	 * looking in further locations.
	 * </p>
	 *
	 * @param nonHintedBaseName
	 *            the non hinted base name of the library to load
	 * @return true if the proper library was found and loaded, false otherwise
	 */
	public boolean loadLibrary(String resourcePath, String nonHintedBaseName) {
		if (osClass == null) {
			logger.error("Unknown operating system! Can't resolve library name");
			return false;
		}
		if (architecture == null) {
			logger.error("Unknown CPU/JVM architecture! Can't resolve library name");
			return false;
		}

		logger.debug("try to load hinted lib from resource");

		final String hintedBaseName = nonHintedBaseName + "-" + osClass + "-" + architecture;
		final String nonHintedLibraryName = mapLibraryName(nonHintedBaseName);
		final String hintedLibraryName = mapLibraryName(hintedBaseName);
		final String hintedFullLibPath = resourcePath + "/" + hintedLibraryName;
		final String nonHintedFullLibPath = resourcePath + "/" + osClass + "-" + architecture + "/"
				+ nonHintedLibraryName;

//		System.out.println("nonHintedFullLibPath:" +nonHintedFullLibPath);
//		System.out.println("hintedFullLibPath:" +hintedFullLibPath);
//		System.out.println("nonHintedLibraryName:" +nonHintedLibraryName);
//		System.out.println("hintedBaseName:" +hintedBaseName);

		logger.debug("try to load hinted lib from default library path");
		try {
			System.loadLibrary(hintedBaseName);
			logger.debug("Loaded hinted lib "+hintedLibraryName+" from library path");
			return true;
		} catch (UnsatisfiedLinkError ex) {
			logger.debug("hinted lib {0} not in library path", hintedLibraryName);
		}

		if (isAvailableInJar(nonHintedFullLibPath)) {
			try {
				final String libCanonicalName = extractLibFromJar(resourcePath, nonHintedFullLibPath);
				System.load(libCanonicalName);
				logger.debug("Loaded JNI lib "+hintedLibraryName+" (extracted from resource)");
				return true;
			} catch (IOException ex) {
				logger.debug("Could not extract lib from JAR", ex);
			}
		} else {
			logger.debug("JNI lib " + hintedLibraryName + " not in resource at " + nonHintedFullLibPath);
		}
		if (isAvailableInJar(hintedFullLibPath)) {
			try {
				final String libCanonicalName = extractLibFromJar(resourcePath, hintedLibraryName);
				System.load(libCanonicalName);
				logger.debug("Loaded JNI lib "+libCanonicalName+" (extracted from resource)");
				return true;
			} catch (IOException ex) {
				logger.debug("Could not extract lib from JAR", ex);
			}
		} else {
//			logger.warn("JNI lib " + hintedLibraryName + " not in resource at " + hintedFullLibPath);
		}

//		logger.debug("try to load non-hinted lib from default library path");
//		try {
//			System.loadLibrary(nonHintedBaseName);
//			logger.debug("Loaded non-hinted lib {0} from library path", nonHintedBaseName);
//			return true;
//		} catch (UnsatisfiedLinkError ex) {
//			logger.debug("non-hinted lib {0} not in library path", nonHintedBaseName);
//		}
		logger.debug("Stopping search. No implementation of JNI library " + nonHintedBaseName
				+ " found for architecture " + architecture + " on OS " + osClass + ".");
		return false;
	}

	/**
	 * Extracts a file from resources.
	 *
	 * <p>
	 * Uses the library loader class loader to copy a resource file to a
	 * temporary file.
	 * </p>
	 *
	 * @param path
	 *            the path to the resource
	 * @param fileName
	 *            the name of the resource
	 * @return the name of the new temporary file
	 * @throws IOException
	 *             if the extraction failed
	 */
	private String extractLibFromJar(final String path, final String fileName) throws IOException {
		// int extPosition = fileName.lastIndexOf('.');
		// final String prefix = fileName.substring(0, extPosition) + "-";
		// final String extension = fileName.substring(extPosition);
		final File libraryFile = File.createTempFile(fileName, null);
		InputStream inputStream = classLoader.getResourceAsStream(path + "/" + fileName);
		libraryFile.deleteOnExit();
		// File libraryFile = new File(fileName);
		libraryFile.deleteOnExit();
		FileOutputStream fileOutputStream = new FileOutputStream(libraryFile);
		byte[] buffer = new byte[8192];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) > 0) {
			fileOutputStream.write(buffer, 0, bytesRead);
		}
		fileOutputStream.close();
		inputStream.close();
		return libraryFile.getAbsolutePath();
	}

	/**
	 * Tests if a given file name is available in the resources of the library
	 * loader class loader.
	 *
	 * @param fileName
	 *            the file name of the resource file
	 * @return true if the file is available, false otherwise
	 */
	private boolean isAvailableInJar(final String fileName) {
		// System.out.println(classLoader.toString()+","+fileName);
		return (classLoader.getResource(fileName) != null);
	}

	/**
	 * Maps the current operating system into an operating system class.
	 *
	 * There are many different os.name values for operating systems. Many of
	 * them can be bundled into groups. e.g. we don't care about the exact
	 * Windows version.
	 *
	 * @return the os class name
	 */
	private String determineOsClass() {
		final String osName = System.getProperty("os.name");

		if ("Windows 95".equals(osName) || "Windows 98".equals(osName) || "Windows Me".equals(osName)
				|| "Windows NT".equals(osName) || "Windows NT (unknown)".equals(osName) // Vista
																						// on
																						// Java
																						// 1.4
				|| "Windows Vista".equals(osName) // Vista on newer Java
				|| "Windows XP".equals(osName) || "Windows 2000".equals(osName) || "Windows 2003".equals(osName)
				|| (osName.indexOf("Windows") >= 0)) {
			return OS_WINDOWS;
		} else if ("Mac OS X".equals(osName)) {
			return OS_MACOSX;
		} else if ("Linux".equals(osName)) {
			return OS_LINUX;
		} else {
			logger.info("The os.name value {0} is unknown. Please file a bug.", osName);
			return null;
		}
	}

	/**
	 * Maps the current platform architecture into a class of architectures.
	 *
	 * There are many different os.arch values for one architecture under
	 * different JVMs. e.g. amd64 and x86_64 are two names for the same
	 * platform.
	 *
	 * @return the architecture class name
	 */
	private String determineArchitecture() {
		final String osArch = System.getProperty("os.arch");

		if ("x86".equals(osArch) || "i386".equals(osArch) || "i486".equals(osArch) || "i586".equals(osArch)
				|| "i686".equals(osArch)) {
			return ARCH_X86;
		} else if ("x86_64".equals(osArch) || "amd64".equals(osArch)) {
			return ARCH_X86_64;
		} else {
			logger.info("The os.arch value {0} is unknown. Please file a bug.", osArch);
			return null;
		}
	}

	/**
	 * Maps the library base name to the platform specific library name.
	 *
	 * Some operating systems allow multiple library naming conventions, but the
	 * architecture of {@link System.mapLibraryName} does not allows this. On
	 * some os like mac OSX the default library mapping depends on the JVM
	 * version, but on all JVMs both library variants can be actually loaded.
	 * This wrapper ensures, that the correct naming convention is used,
	 * independent from the JVM version.
	 *
	 * @param baseName
	 *            the library base name
	 * @return the platform specific library name
	 * @see System.mapLibraryName
	 */
	private String mapLibraryName(String baseName) {
		if (osClass == OS_MACOSX) {
			/*
			 * On mac OSX System.mapLibraryName uses .jnilib or .dylib extension
			 * depending on the used JVM version. (Java 7 will use .dylib) This
			 * forces the use of .jnilib for all versions.
			 */
			return "lib" + baseName + ".jnilib";
		} else {
			return System.mapLibraryName(baseName);
		}
	}
}
