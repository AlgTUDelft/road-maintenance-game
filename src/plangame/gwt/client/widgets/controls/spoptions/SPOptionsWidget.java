/**
 * @file SPOptionsWidget.java
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
 * @date         27 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls.spoptions;

import java.util.ArrayList;
import java.util.List;

import plangame.gwt.client.resource.locale.Lang;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a new options widget
 *
 * @author Joris Scharpff
 */
public class SPOptionsWidget extends Composite {
	/** The main panel */
	protected VerticalPanel pnlMain;
	
	/** The TTL display format checkbox */
	protected CheckBox chkTTLRelative;
	
	/** Show TTL distribution on the map */
	protected CheckBox chkTTLOnMap;
	
	/** The listener(s) for option changes */
	protected List<OptionChangeListener> listeners;

	/**
	 * Creates the new options widget
	 */
	public SPOptionsWidget( ) {
		// initialise listeners list
		listeners = new ArrayList<OptionChangeListener>( );
		
		// create the panel
		pnlMain = new VerticalPanel( );
		
		// add the options
		chkTTLRelative = new CheckBox( Lang.text.W_SPOptions_TTLRelative( ) );
		chkTTLRelative.addValueChangeHandler( new ValueChangeHandler<Boolean>( ) {
			@Override public void onValueChange( ValueChangeEvent<Boolean> event ) {
				fireChange( chkTTLRelative );
			}
		} );
		pnlMain.add( chkTTLRelative );
		
		chkTTLOnMap = new CheckBox( Lang.text.W_SPOptions_TTLOnMap( ) );
		chkTTLOnMap.addValueChangeHandler( new ValueChangeHandler<Boolean>( ) {
			@Override public void onValueChange( ValueChangeEvent<Boolean> event ) {
				fireChange( chkTTLOnMap );
			}
		} );
		pnlMain.add( chkTTLOnMap );
		
		initWidget( pnlMain );
	}
	
	/**
	 * Adds a listener to the list of option change listeners
	 * 
	 * @param listener The listener to add
	 */
	public void addListener( OptionChangeListener listener ) {
		listeners.add( listener );
	}
	
	/**
	 * Called whenever any of the options changes
	 * 
	 * @param widget The widget that changed
	 */
	private void fireChange( Widget widget ) {
		for( OptionChangeListener ol : listeners ) {
			if( widget == chkTTLRelative )
				ol.onSetTTLRelative( chkTTLRelative.getValue( ) );
			else if( widget == chkTTLOnMap )
				ol.onSetTTLOnMap( chkTTLOnMap.getValue( ) );
		}
	}
	
	/**
	 * Sets the value for the show relative option
	 * 
	 * @param relative True to enable relative display
	 */
	public void setTTLRelative( boolean relative ) {
		chkTTLRelative.setValue( relative );
		fireChange( chkTTLRelative );
	}
	
	/**
	 * @return True if TTL should be displayed relative
	 */
	public boolean isTTLRelative( ) {
		return chkTTLRelative.getValue( );
	}
	
	/**
	 * Sets the value for the show TTL on map option
	 * 
	 * @param onmap True to set display of TTL on the map
	 */
	public void setTTLOnMap( boolean onmap ) {
		chkTTLOnMap.setValue( onmap );
		fireChange( chkTTLOnMap );
	}
	
	/**
	 * @return True if the TTL should be displayed on the map
	 */
	public boolean isTTLOnMap( ) {
		return chkTTLOnMap.getValue( );
	}
}
