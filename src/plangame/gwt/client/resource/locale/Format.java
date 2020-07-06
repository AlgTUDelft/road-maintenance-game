/**
 * @file Format.java
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
package plangame.gwt.client.resource.locale;

import plangame.model.quality.QualityLevel;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Text formatting for user display
 *
 * @author Joris Scharpff
 */
public class Format {
	/**
	 * Possible number formats
	 */
	public enum Style {
		/** Currency: [CurrSym] #,##0.00 */
		Curr,
		/** Currency in thousands: [CurrSym] #,##0 K */
		CurrK,
		/** 2 Digit decimal: 0.00 */
		Dec2,
		/** Standard integer: 0 */
		Int,		
		/** Large integer: #,##0 */
		BigInt,
		/** Integer in thousands: #,##0 K */
		IntK,
		/** Percentage: 0 % */
		Percentage,
		/** Percentage with 2 decimals: 0.00 % */
		Percentage2,
		/** Quality: 0.0 */
		Quality,
		/** Ranking 1st, 2nd, 3rd etc.*/
		Rank,
		/** Duration in weeks: 0 [Weeks] */
		Weeks;
	}
	
	/**
	 * Formats the value using the specified formatting
	 * 
	 * @param value The value
	 * @param style The formatting style
	 * @return The formatted string
	 */
	public static String f( double value, Style style ) {
		switch( style ) {
			case Curr:
				return NumberFormat.getFormat( Lang.text.Format_Currency( ) ).format( value );

			case CurrK:
				return NumberFormat.getFormat( Lang.text.Format_CurrencyK( ) ).format( value / 1000 );
				
			case Dec2:
				return fDec( value, 2 );
				
			case Int:
				return "" + (int)value;
				
			case BigInt:
				return NumberFormat.getFormat( Lang.text.Format_BigInt( )  ).format( value );
				
			case IntK:
				return NumberFormat.getFormat( Lang.text.Format_IntK( ) ).format( value / 1000 );
			
			// FIXME locale
			case Percentage:
				return NumberFormat.getFormat( "0" ).format( value * 100.0 ) + " %";

			// FIXME locale
			case Percentage2:
				return NumberFormat.getFormat( "0.00" ).format( value * 100.0 ) + " %";
				
			case Rank:
				// exceptions in the English language
				if( value == 1 && LocaleInfo.getCurrentLocale( ).getLocaleName( ).equalsIgnoreCase( "en" ) )
					return NumberFormat.getFormat( "0" ).format( value ) + "st";
				else if( value == 2 && LocaleInfo.getCurrentLocale( ).getLocaleName( ).equalsIgnoreCase( "en" ) )
					return NumberFormat.getFormat( "0" ).format( value ) + "nd";
				else					
					return NumberFormat.getFormat( Lang.text.Format_Rank( ) ).format( value );

			// FIXME locale
			case Quality:
				return NumberFormat.getFormat( "0.0" ).format( value );
				
			case Weeks:
				return NumberFormat.getFormat( Lang.text.Format_Weeks( ) ).format( value );

			default:
				return "" + value;
		}
	}
	
	/**
	 * Formats a quality level in the correct format
	 * 
	 * @param qlevel The quality level
	 * @return The quality string
	 */
	public static String f( QualityLevel qlevel ) {
		return f( qlevel.getQuality( ), Style.Quality );
	}
	
	/**
	 * Formats the values as v1 [+/- v2] using the specified formatting
	 * 
	 * @param v1
	 * @param v2
	 * @param style The formatting style
	 * @return The formatted string
	 */
	public static String fb( double v1, double v2, Style style ) {
		return f( v1, style ) + " [" + (v2 >= 0 ? "+" : "-") + " " + f( v2, style ) + "]";
	}
	
	/**
	 * Formats the values as v1 - (v1 + v2)
	 * 
	 * @param v1
	 * @param v2
	 * @param style The formatting style
	 * @return The formatted string
	 */
	public static String f( double v1, double v2, Style style ) {
		return f( v1, style ) + " - " + f( v1 + v2, style );		
	}
	
	/**
	 * @param dec The decimal to format
	 * @param n The number of decimal digits
	 * @return Formatted decimal using n digits after the decimal separator
	 */
	public static String fDec( double dec, int n ) {
		String decs = ""; 
		for( int i = 0; i < n; i++ )
			decs += "0";
		
		return NumberFormat.getFormat( "0." + decs ).format( dec );
	}	
}
