/**
 * @file HyperLabel.java
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
 * @date         28 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;

/**
 * Extends the GWT Label to allow for enable/disabling of the label
 *
 * @author Joris Scharpff
 */
public class HyperLabel extends Label implements HasEnabled {
	/** True if the label is enabled */
	protected boolean enabled;
	
	/** True if hover event should be registered */
	protected boolean hover;
	
	/**
	 * Creates a new HyperLabel
	 * 
	 * @param caption The caption
	 */
	public HyperLabel( String caption ) {
		super( caption );
		
		setHoverable( false );
		setEnabled( true );
		setStylePrimaryName( "hyperlabel" );

		// add mouse over listener
		addMouseOverHandler( new MouseOverHandler( ) {
			@Override public void onMouseOver( MouseOverEvent event ) {
				mouseOver( true );
			}
		} );
		
		// and mouse out
		addMouseOutHandler( new MouseOutHandler( ) {
			@Override public void onMouseOut( MouseOutEvent event ) {
				mouseOver( false );
			}
		} );
	}
	
	/**
	 * Creates a new HyperLabel
	 */
	public HyperLabel( ) {
		this( "" );
	}
	
	/**
	 * Creates a new HyperLabel from HTML
	 * 
	 * @param caption The safe HTML
	 */
	public HyperLabel( SafeHtml caption ) {
		this( caption.asString( ) );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
		
		setStyleDependentName( "disabled", !enabled );
    DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);		
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled( ) {
		return enabled;
	}
	
	/**
	 * Set to true to allow hover events on this hyperlabel
	 * 
	 * @param hover True to allow hover events
	 */
	public void setHoverable( boolean hover ) {
		this.hover = hover;
	}
	
	/**
	 * @return True if the label registers hover events
	 */
	public boolean isHoverable( ) {
		return hover;
	}
	
	/**
	 * Sets or unsets the mouse over property
	 * 
	 * @param in True if the mouse is over the label
	 */
	private void mouseOver( boolean in ) {
		setStyleDependentName( "hoover", in & isEnabled( ) & isHoverable( ) );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.Label#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler( ClickHandler handler ) {
		// add clickable style suffix
		addStyleDependentName( "clickable" );
		
		return super.addClickHandler( handler );
	}
}
