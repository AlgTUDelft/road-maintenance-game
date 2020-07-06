/**
 * @file Lang.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         19 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.resource.locale;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Server side locale
 *
 * @author Joris Scharpff
 */
public class Lang {
	/** The text bundle */
	protected static ResourceBundle textbundle;
	
	/** The current locale for the language */
	protected static Locale locale;
	
	/**
	 * Sets the locale, invalidates the bundle so that is is reloaded
	 * 
	 * @param code The ISO code of the locale
	 */
	public static void setLocale( String code ) {
		locale = new Locale( code );
		textbundle = null;
	}
	
	/**
	 * Retrieves the locale, uses Locale.ENGLISH if the locale is not set
	 * 
	 * @return The current locale
	 */
	public static Locale getLocale( ) {
		if( locale == null )
			locale = Locale.ENGLISH;
		
		return locale;
	}
	
	/**
	 * Retrieves the bundle
	 * 
	 * @return The active bundle, loads it on first access
	 */
	private static ResourceBundle getTextBundle( ) {		
		if( textbundle == null )
			textbundle = ResourceBundle.getBundle( Lang.class.getPackage( ).getName( ) + ".lang", getLocale( ) );
		
		return textbundle;
	}
	
	/**
	 * Retrieves the text string from the file, asserts the existence of the
	 * string to prevent typo/mismatch problems
	 * 
	 * @param key The text string key
	 * @return The text string value
	 */
	public static String get( String key ) {
		try {
			return getTextBundle( ).getString( key );
		} catch( MissingResourceException mre ) {
			assert false : "No translation available for the key '" + key + "'"; 
			return null;
		}
	}
	
	/**
	 * Retrieves the text string from the file and fills in the arguments.
	 * Asserts the existence of the string to prevent typo/mismatch errors
	 * 
	 * @param key The text string key
	 * @param args The text string arguments to be inserted
	 * @return The text string
	 */
	public static String get( String key, String... args ) {
		// setup formatted
		MessageFormat formatter = new MessageFormat( getTextBundle( ).getString( key ) );
		formatter.setLocale( getLocale( ) );
		
		// insert arguments
		try {
			return formatter.format( args );
		} catch( IllegalArgumentException iae ) {
			assert false : iae.getMessage( );
			return null;
		}
	}
}
