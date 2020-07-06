/**
 * @file KeyLabel.java
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
package plangame.gwt.client.widgets.controls;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a key and value pair using labels
 *
 * @author Joris Scharpff
 */
public class KeyLabel extends Composite {
	/** The panel containing the two labels */
	protected HorizontalPanel panel;
	
	/** The key label */
	protected Label keylabel;
	
	/** The value label */
	protected Label valuelabel;
	
	/**
	 * Creates a new key label pair
	 */
	public KeyLabel( ) {		
		// create the elements
		panel = new HorizontalPanel( );
		keylabel = new Label( );
		valuelabel = new Label( );
		
		// add the labels
		panel.add( keylabel );
		panel.add( valuelabel );
		
		// add style classes
		keylabel.addStyleName( "keylabel-key" );
		keylabel.addStyleName( "keylabel-value" );
		
		// set sizes of labels
		keylabel.setSize( "50%", "100%" );
		valuelabel.setSize( "50%", "100%" );
		
		// initialise the widget
		initWidget( panel );
	}
	
	/**
	 * Creates a new key label pair with the given text
	 * 
	 * @param keytext The key label text
	 * @param valuetext The value label text
	 */
	public KeyLabel( String keytext, String valuetext ) {
		this( );
		
		setKeyText( keytext );
		setValueText( valuetext );
	}
	
	/**
	 * Sets the size of the panel in pixels
	 * 
	 * @param width The new width
	 * @param height The new height
	 */
	public void setSize( int width, int height ) {
		panel.setSize( width + "px", height + "px" );
	}
	
	/**
	 * Sets the text of the key label
	 * 
	 * @param text The text
	 */
	public void setKeyText( String text ) {
		keylabel.setText( text );
	}
	
	/**
	 * @return The text that is displayed on the key label
	 */
	public String getKeyText( ) {
		return keylabel.getText( );
	}
	
	/**
	 * Sets the text of the value label
	 * 
	 * @param text The text
	 */
	public void setValueText( String text ) {
		valuelabel.setText( text );
	}
	
	/**
	 * @return The text that is displayed on the value label 
	 */
	public String getValueText( ) {
		return valuelabel.getText( );
	}
}
