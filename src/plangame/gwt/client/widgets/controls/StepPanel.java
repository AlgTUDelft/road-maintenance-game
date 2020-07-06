/**
 * @file StepPanel.java
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

import java.util.Iterator;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for dialog steps
 *
 * @author Joris Scharpff
 * @param <T> The panel value
 */
public abstract class StepPanel<T> extends VerticalPanel {
	/** Current display mode */
	protected boolean active;
	
	/** The value of the panel */
	protected T value;
	
	/** The panel title */
	protected HyperLabel lblTitle;
	
	/** The selected panel value */
	protected HyperLabel lblValue;
	
	/** The previous step anchor */
	protected HyperLabel lnkPrev;
	
	/** The next step anchor */
	protected HyperLabel lnkNext;
	
	/** The 'main' panel containing the widgets of this step */
	protected VerticalPanel pnlMain;
	
	/** Title panel */
	protected HorizontalPanel pnlTitle;
	
	/** Panel containing the control anchors */
	protected HorizontalPanel pnlControl;
	
	/**
	 * Creates a new step panel
	 */
	public StepPanel( ) {
		super( );
		
		// create the title panel
		pnlTitle = new HorizontalPanel( );
		pnlTitle.addStyleName( "steppanel-titlepanel" );
		pnlTitle.setWidth( "100%" );
		
		// create title label
		lblTitle = new HyperLabel( );
		lblTitle.addStyleName( "steppanel-title" );
		pnlTitle.add( lblTitle );
		
		// and value label
		lblValue = new HyperLabel( );
		lblValue.addStyleName( "steppanel-value" );
		lblValue.setVisible( false );
		pnlTitle.add( lblValue );
		
		// create the control panel, always show controls
		pnlControl = new HorizontalPanel( );
		pnlControl.addStyleName( "steppanel-controls" );
		pnlControl.setWidth( "96%" );
		pnlControl.getElement( ).getStyle( ).setProperty( "margin", "0px auto" );
		pnlTitle.add( pnlControl );

		// add the title panel
		addWidget( pnlTitle );
		
		// initialise the controls
		lnkPrev = new HyperLabel( "<< Previous" );
		lnkPrev.setHoverable( true );
		lnkPrev.addStyleName( "steppanel-prev" );
		lnkPrev.setWidth( "100%" );
		lnkPrev.setVisible( false );
		pnlControl.add( lnkPrev );
		lnkNext = new HyperLabel( "Next >>" );
		lnkNext.setHoverable( true );
		lnkNext.addStyleName( "steppanel-next" );
		lnkNext.getElement( ).getStyle( ).setTextAlign( TextAlign.RIGHT );
		lnkNext.setWidth( "100%" );
		lnkNext.setVisible( false );
		pnlControl.add( lnkNext );
		
		// create the main panel
		pnlMain = new VerticalPanel( );
		pnlMain.setWidth( "100%" );
		pnlMain.addStyleName( "steppanel-mainpanel" );
		addWidget( pnlMain );
		
		// set to inactive
		setActive( false );
	}
	
	/**
	 * Creates the panel with the specified title
	 * 
	 * @param title The title of the step panel
	 */
	public StepPanel( String title ) {
		this( );
		
		setStepTitle( title );
	}
	
	/**
	 * Sets the value of the step panel
	 * 
	 * @param value The value
	 */
	public void setValue( T value ) {
		this.value = value;
		lblValue.setText( (value != null ? getValueText( value ) : null) );		
		
		// update the panel
		onValueChange( value );
	}
	
	/**
	 * @return The current value for the panel
	 */
	public T getValue( ) {
		return value;
	}
	
	/**
	 * Adds the control directly to the widget, only for static panel elements
	 * 
	 * @param widget The widget to add
	 */
	private void addWidget( Widget widget ) {
		super.add( widget );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.VerticalPanel#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add( Widget w ) {
		// add controls to the main panel
		pnlMain.add( w );		
	}
	
	/**
	 * Shows or hides the previous and next buttons, depending on whether they
	 * have been assigned a handler or not and the panel is active
	 * 
	 * @param active True if the panel is active
	 */
	private void showControls( boolean active ) {
		// reset control panel based on the selected controls
		pnlControl.setVisible( active && (lnkPrev.isVisible( ) | lnkNext.isVisible( )) );
	}
	
	/**
	 * Activates or deactivates the panel
	 * 
	 * @param active True to activate the panel
	 */
	public void setActive( boolean active ) {
		final boolean wasactive = this.active;
		this.active = active;
		
		// add style names for active state
		setStyleDependentName( "active", active );
		setStyleDependentName( "inactive", !active );
		
		// show the controls if active, else the value is shown
		showControls( active );
		lblValue.setVisible( !active );
		
		// show or hide the main panel
		pnlMain.setVisible( active );
		
		// enable or disable all child widgets
		setEnabled( active );
		
		// call (de)activate event for the panel if it is now active
		if( !wasactive && active )
			onPanelActivate( );
		else if( wasactive && !active )
			onPanelDeactivate( );
	}
	
	/**
	 * @return Whether this panel is active
	 */
	public boolean isActive( ) {
		return active;
	}
	
	/**
	 * Enables or disables all child widgets of this panel
	 * 
	 * @param enabled True to enable, false to disable
	 */
	public void setEnabled( boolean enabled ) {
		setEnabled( this, enabled );
	}
	
	/**
	 * Recursively enables/disables all child widgets
	 * 
	 * @param widget The widget to enable/disable
	 * @param enabled True to enable, false to disable
	 */
	private void setEnabled( Widget w, boolean enabled ) {
		// check if the widget has any child widgets
		if( w instanceof HasWidgets ) {
			Iterator<Widget> iter = ((HasWidgets)w).iterator( );
      while( iter.hasNext( ) )
      {
      	// get next widget and recursively en/disable all child widgets
        Widget nextWidget = iter.next( );
        setEnabled( nextWidget, enabled );

        // enable/disable this widget if possible
        if( nextWidget instanceof HasEnabled ) {
          ((HasEnabled)nextWidget).setEnabled( enabled );
        }
      }
    }
	}
	
	/**
	 * Adds the previous button to the panel
	 * 
	 * @param caption The button caption
	 * @param handler The button handler
	 */
	public void addPrevious( String caption, ClickHandler handler ) {
		// enable the control 
		lnkPrev.setVisible( true );
		lnkPrev.setText( caption );
		lnkPrev.addClickHandler( handler );
		
		// update controls
		showControls( isActive( ) );
	}
	
	/**
	 * Adds the next button to the panel
	 * 
	 * @param caption The button caption
	 * @param handler The button handler
	 */
	public void addNext( String caption, ClickHandler handler ) {
		// enable the control
		lnkNext.setVisible( true );
		lnkNext.setText( caption );
		lnkNext.addClickHandler( handler );
		
		// update controls
		showControls( isActive( ) );
	}
	
	/**
	 * Sets the panel title
	 * 
	 * @param title The step panel title
	 */
	public void setStepTitle( String title ) {
		lblTitle.setText( title );
	}
	
	/**
	 * @return The step panel title
	 */
	public String getStepTitle( ) { return lblTitle.getText( ); }

	/**
	 * @return The displayed step value text
	 */
	public String getStepValueText( ) { return lblValue.getText( ); } 
	
	/**
	 * Sets the visibility of the value label
	 * 
	 * @param visible True to show the value label (only allowed when the panel
	 * is not visible)
	 */
	public void setValueVisible( boolean visible ) {
		lblValue.setVisible( visible && !isActive( ) );
	}
	
	/**
	 * Formats the panel value for display in the label
	 * 
	 * @param value The value to format
	 * @return The formatted value 
	 */
	public abstract String getValueText( T value );
	
	/**
	 * Called whenever the panel value changes
	 * 
	 * @param value The new value
	 */
	public abstract void onValueChange( T value );
	
	/**
	 * Called whenever the panel becomes active
	 */
	public void onPanelActivate( ) {
		
	}
	
	/**
	 * Called whenever the panel becomes inactive
	 */
	public void onPanelDeactivate( ) {
		
	}
	
}
