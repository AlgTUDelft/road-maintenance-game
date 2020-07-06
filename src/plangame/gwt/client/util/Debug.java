/**
 * @file Debug.java
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
 * @date         16 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;

import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.gwt.client.ClientView;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.LogType;

/**
 * Common debug functions, the class is marked deprecated to easily find uses
 * in the source code. All functions will respect the isDebug( ) parameter of
 * the {@link DebugGlobals} class
 *
 * @author Joris Scharpff
 */
@Deprecated
public class Debug {
	/**
	 * DEBUG Prints the contents of the list, elements are printed using the
	 * toString method. Checks if debug mode is running
	 * 
	 * @param list The list to print
	 */
	public static void printList( List<?> list ) {
		if( !DebugGlobals.isDebug( ) ) return;
		
		String s = "List contents (size " + list.size( ) + "):\n";
		for( int i = 0; i < list.size( ); i++ )
			s += toString( list.get( i ) ) + "\n";
		print( s );
	}
	
	/**
	 * DEBUG prints the objects to sysout, if debug mode is running. The toString
	 * method is used to get the strings for each object and they are separated
	 * by a space
	 * 
	 * @param objects The objects to print
	 */
	public static void print( Object... objects ) {
		if( !DebugGlobals.isDebug( ) ) return;
		
		for( Object o : objects ) {
			System.err.print( toString( o ) + " " );
		}
		System.err.println( );
	}
	
	/**
	 * DEBUG prints the objects through the log, if debug mode is running. The toString
	 * method is used to get the strings for each object and they are separated
	 * by a space
	 * 
	 * @param objects The objects to print
	 */
	public static void logprint( Object... objects ) {
		if( !DebugGlobals.isDebug( ) ) return;
		
		String msg = "";
		for( Object o : objects ) {
			msg += toString( o ) + " ";
		}
		ClientView.getInstance( ).log( LogType.Debug, msg );
	}
	
	/**
	 * DEBUG prints the elements of an array if the debug mode if running.
	 * Elements are comma separated and are printed using the toString method
	 * 
	 * @param array The array to print
	 */
	public static void printArray( double[] array ) {
		if( !DebugGlobals.isDebug( ) ) return;

		String s = "";
		for( int i = 0; i < array.length; i++ )
			s += array[i] + " ";
		s += "\n";
		
		print( s );
	}
	
	/**
	 * DEBUG prints the elements of an array if the debug mode if running.
	 * Elements are comma separated and are printed using the toString method
	 * 
	 * @param array The array to print
	 */
	public static void printArray( Object[] array ) {
		if( !DebugGlobals.isDebug( ) ) return;

		String s = "";
		for( int i = 0; i < array.length; i++ )
			s += toString( array[i] ) + " ";
		s += "\n";
		
		print( s );
	}
	
	/**
	 * DEBUG prints the element of a matrix if the debug mode is running.
	 * 
	 * @param matrix The matrix
	 * @param N The number of rows
	 * @param M The number of columns
	 */
	public static void printMatrix( Object[][] matrix, int N, int M ) {
		if( !DebugGlobals.isDebug( ) ) return;

		String s = "";
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < M; j++ ) 
				s += toString( matrix[i][j] ) + " ";
			s += "\n";
		}
		
		print( s );
	}
	
	/**
	 * DEBUG prints the element of a matrix if the debug mode is running.
	 * 
	 * @param matrix The matrix
	 * @param N The number of rows
	 * @param M The number of columns
	 */
	public static void printMatrix( double[][] matrix, int N, int M ) {
		if( !DebugGlobals.isDebug( ) ) return;

		String s = "";
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < M; j++ ) 
				s += matrix[i][j] + " ";
			s += "\n";
			
			print( s );
		}
	}
	/**
	 * DEBUG prints a joint plan
	 * 
	 * @param jplan The joint plan to print
	 */
	public static void printPlan( JointPlan jplan ) {
		jplan.print( );
	}
	
	/**
	 * Returns the toString of the object or (null) if the object is null
	 * 
	 * @param object The object
	 * @return The toString'ed object
	 */
	private static String toString( Object object ) {
		return (object != null ? object.toString( ) : "(null)");
	}
}
