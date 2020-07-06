/**
 * @file GameViewExceptionHandler.java
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
 * @date         6 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.UmbrellaException;

/**
 * Handler for uncaught GWT exceptions
 *
 * @author Joris Scharpff
 */
public class ClientExceptionHandler implements UncaughtExceptionHandler {
	/**
	 * @see com.google.gwt.core.client.GWT.UncaughtExceptionHandler#onUncaughtException(java.lang.Throwable)
	 */
	@Override
	public void onUncaughtException( Throwable e ) {
		// unwrap possible Umbrella exceptions
		final Throwable ue = unwrapException( e );
		ue.printStackTrace( );
	}

	/**
	 * Unwraps the exception
	 * 
	 * @param e The uncaught exception
	 * @return The unwrapped exception
	 */
  public Throwable unwrapException(Throwable e) {   
    if( e instanceof UmbrellaException ) {   
      UmbrellaException ue = (UmbrellaException)e;  
      if( ue.getCauses( ).size( ) == 1 ) {   
        return unwrapException( ue.getCauses( ).iterator( ).next( ) );  
      }  
    }  
    return e;  
  }  

}
