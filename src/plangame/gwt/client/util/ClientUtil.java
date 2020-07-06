/**
 * @file ClientUtil.java
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
 * @date         21 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;

import java.util.List;

import plangame.game.plans.PlanError;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.shared.clients.Client.ClientType;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * General utiliy functions shared by all client views
 *
 * @author Joris Scharpff
 */
public class ClientUtil {
	/**
	 * Asserts that the current client is of the specified type
	 * 
	 * @param type The required client type
	 */
	public static void assertType( ClientType type ) {
		assert (ClientView.ofType( type )) : "Operation not supported for the client type " + ClientView.getInstance( ).getClientType( ).toString( );
	}
	
	/**
	 * Adds or changes the token to the history of the client, stored in the URL
	 * 
	 * @param token The token name
	 * @param value The new value
	 * @return The previous token value (null if no previous value was set)
	 */
	public static String setToken( String token, String value ) {
		// get current token string
		final String currtoken = History.getToken( );
		
		// check if the token is present
		final String oldval = getToken( token );
		if( oldval != null ) {
			// replace value
			final String oldtoken = token + "=" + oldval;
			final String left = currtoken.substring( 0, currtoken.indexOf( oldtoken ) );
			final String right = currtoken.substring( currtoken.indexOf( oldtoken ) + oldtoken.length( ) );
			
			History.newItem( left + token + "=" + value + right );			
		} else {
			// append the new token to the history string
			History.newItem( currtoken + ";" + token + "=" + value ); 
		}
		return oldval;
	}
	
	/**
	 * Retrieves the token value from the current session URL
	 * 
	 * @param token The token name
	 * @return The token value or null if no value is known
	 */
	public static String getToken( String token ) {
		final String[] tokens = History.getToken( ).split( ";" );
		for( String t : tokens )
			if( t.startsWith( token ) )
				return t.substring( t.indexOf( "=" ) + 1 );
		
		return null;		
	}
	
	/**
	 * Creates a wait dialog
	 * 
	 * @param title The dialog title
	 * @param msg The wait message
	 * @return The dialog
	 */
	public static PopupPanel createDialog( String title, String msg ) {
		final PopupPanel p = new PopupPanel( false, true );
		
		// FIXME: CSS for the panel?
		//p.setStyleName( "waitdialog" );
		p.setSize( "300px", "200px" );
		p.setGlassEnabled( true );
		p.setTitle( title );
		p.setPopupPosition( Window.getClientWidth( ) / 2 - 150, Window.getClientHeight( ) / 2 - 100 );
		p.add( new Label( msg ) );
		return p;
	}
	

	/**
	 * Function to prevent selection of the text
	 * 
	 * @param e The element
	 */
	// FIXME does not work very well
	public native static void disableTextSelect( Element e )/*-{
//    e.ondrag = function () { return false; };
//    e.onselectstart = function () { return false; };
//    e.style.MozUserSelect = "none";
	}-*/;

	/**
	 * Retrieves the localised message for the plan errors
	 * 
	 * @param errormsg The accompanying error message
	 * @param errors The plan errors
	 * @return The localised text message for the exception
	 */
	public static String getLocalisedPlanErrors( String errormsg, List<PlanError> errors ) {
		// build error string
		String emsg = errormsg + "\n";
		
		// get localised message for all of the errors
		for( PlanError e : errors ) {
			final String local;
			switch( e.getType( ) ) {
				case AlreadyDelayed:
					local = Lang.text.PlanError_AlreadyDelayed( e.getMethod( ).toString( ) );
					break;
					
				case DifferentTasks:
					// no need for full method name here, use description
					local = Lang.text.PlanError_DifferentTasks( e.getMethod( ).getDescription( ), e.getMethod2( ).getDescription( ) );
					break;
					
				case Executed:
					local = Lang.text.PlanError_Executed( e.getMethod( ).toString( ) );
					break;
					
				case Overlap:
					local = Lang.text.PlanError_Overlap( e.getMethod( ).toString( ), e.getMethod2( ).toString( ) );
					break;
					
				case PlanInPast:
					local = Lang.text.PlanError_PlanInPast( e.getMethod( ).toString( ) );
					break;
					
				case TaskAlreadyPlanned:
					// no need for full method name
					local = Lang.text.PlanError_TaskAlreadyPlanned( e.getMethod( ).getDescription( ), e.getMethod2( ).getDescription( ) );
					break;
					
				case UnableToComplete:
					local = Lang.text.PlanError_UnableToComplete( e.getMethod( ).toString( ) );
					break;
					
				default:
					local = "Unknown plan error!";
					break;
			}
			
			emsg += "\n- " + local;
		}
		
		return emsg;
	}
}
