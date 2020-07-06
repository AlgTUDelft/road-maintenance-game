/**
 * @file WeekSelect.java
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
 * @date         3 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 *
 * @author Joris Scharpff
 */
public class NumberSelect extends Composite implements HasValueChangeHandlers<Integer> {
	/** The number textbox */
	protected TextBox txtValue;
	
	/** The up button */
	protected Button btnUp;
	
	/** The large up button */
	protected Button btnUpLarge;
	
	/** The down button */
	protected Button btnDown;
	
	/** THe large down button */
	protected Button btnDownLarge;
	
	/** The minimal number value */
	protected int min;
	
	/** The maximal value */
	protected int max;
	
	/** The current value */
	protected int value;
	
	/**
	 * Creates a new numberselect control with default min and max values [1, 10]
	 * and sets the default text to 1
	 */
	public NumberSelect( ) {
		this( 1, 10, 1 );
	}
	
	/**
	 * Creates a new numberselect control and sets the default to min
	 * 
	 * @param min The minimal value
	 * @param max The maximal value
	 */
	public NumberSelect( int min, int max ) {
		this( min, max, min );
	}
	
	/**
	 * Creates a new numberselect control
	 * 
	 * @param min The minimal value
	 * @param max The maximal value
	 * @param def The default value
	 * @throws IllegalArgumentException if min > max
	 * @throws IndexOutOfBoundsException if the default value is not in range
	 */
	public NumberSelect( int min, int max, int def ) {
		if( min > max ) throw new IllegalArgumentException( "Invalid values for min and max: " + min + ", " + max );
		
		// create panel
		final HorizontalPanel panel = new HorizontalPanel( );
		
		// and textbox and buttons
		txtValue = new TextBox( );
		btnDown = new Button( "<" );
		btnDownLarge = new Button( "<<" );
		btnUp = new Button( ">" );
		btnUpLarge = new Button( ">>" );
		panel.add( txtValue );
		panel.add( btnDown );
		panel.add( btnDownLarge );
		panel.add( btnUpLarge );
		panel.add( btnUp );
				
		// init the widget
		initWidget( panel );

		// add listsners
		txtValue.addChangeHandler( new ChangeHandler( ) {
			@Override
			public void onChange( ChangeEvent event ) {
				textChanged( );
			}
		} );
		txtValue.addBlurHandler( new BlurHandler( ) {
			@Override
			public void onBlur( BlurEvent event ) {
				textChanged( );
			}
		} );
		txtValue.addKeyPressHandler( new KeyPressHandler( ) {
			@Override
			public void onKeyPress( KeyPressEvent event ) {
				final int key = event.getNativeEvent( ).getKeyCode( );
				
				if( key == KeyCodes.KEY_ENTER ) {
					textChanged( );
					txtValue.setFocus( false );
					event.stopPropagation( );
				}
			}
		} );
		
		btnDown.addClickHandler( new ClickHandler( ) {
			@Override public void onClick( ClickEvent event ) {
				changeValue( -1 );
			}
		} );
		btnDownLarge.addClickHandler( new ClickHandler( ) {
			@Override public void onClick( ClickEvent event ) {
				changeValue( -5 );				
			}
		} );
		btnUp.addClickHandler( new ClickHandler( ) {
			@Override
			public void onClick( ClickEvent event ) {
				changeValue( 1 );
			}
		} );
		btnUpLarge.addClickHandler( new ClickHandler( ) {
			@Override
			public void onClick( ClickEvent event ) {
				changeValue( 5 );
			}
		} );
		
		// set sizes
		txtValue.setSize( "100%", "100%" );
		btnDown.setSize( "32px", "32px" );
		btnUp.setSize( "32px", "32px" );		
		btnDownLarge.setSize( "32px", "32px" );
		btnUpLarge.setSize( "32px", "32px" );		
				
		// set control values
		this.min = min;
		this.max = max;
		setValue( def );
	}
	
	/**
	 * Sets the minimal value
	 * 
	 * @param min The new minimal
	 * @throws IllegalArgumentException if min > max
	 */
	public void setMin( int min ) {
		if( min > max ) throw new IllegalArgumentException( "Minimal value '" + min + "' exceeds maximum '" + max + "'" );
		
		this.min = min;
		
		// update value if required
		if( value < min ) setValue( min );
	}
	
	/**
	 * Sets the maximal value
	 * 
	 * @param max The new maximum
	 * @throws IllegalArgumentException if max < min
	 */
	public void setMax( int max ) { 
		if( min > max ) throw new IllegalArgumentException( "Minimal value '" + min + "' exceeds maximum '" + max + "'" );
		
		this.max = max;
		
		// update max if required
		if( value > max ) setValue( max );
	}
	
	/**
	 * Sets the value of the number select control, fires an event on change
	 * 
	 * @param value The new value
	 * @throws IndexOutOfBoundsException if the value is not in range
	 */
	public void setValue( int value ) throws IndexOutOfBoundsException {
		setValue( value, true );
	}
		
	/**
	 * Sets the value of the numberselect control
	 * 
	 * @param value The new value
	 * @param event Fire an event on change
	 * @throws IndexOutOfBoundsException if the value is not in range
	 */
	public void setValue( int value, boolean event ) throws IndexOutOfBoundsException {
		if( value < min || value > max ) throw new IndexOutOfBoundsException( "Value out of range: " + value );
		
		final int prev = this.value;
		this.value = value;
		txtValue.setText( "" + value );
		
		if( event && prev != value )
			ValueChangeEvent.fire( this, value );
	}
	
	/**
	 * Retrieves the current value of the numberselect
	 * 
	 * @return The current value
	 */
	public int getValue( ) {
		return value;
	}

	/**
	 * Changes the value by the specified amount, respects min and max values.
	 * The new value will be value + change bounded by [min, max].
	 * 
	 * @param change The change in value
	 */
	private void changeValue( int change ) {
		// update value
		final int newval = getValue( ) + change;
		if( newval > max ) setValue( max );
		else if( newval < min ) setValue( min );
		else setValue( newval );
	}	
	
	/**
	 * Tries to update the value to the new text in the textbox, restores
	 * previous valid value if the text is not a valid number.
	 */
	protected void textChanged( ) {
		// get the new value and try to set it
		try {
			final int newval = Integer.parseInt( txtValue.getText( ) );
			setValue( newval );			
		} catch( Exception nfe ) {
			txtValue.setText( "" + value );
		}
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Integer> handler ) {
		return addHandler( handler, ValueChangeEvent.getType( ) );
	}
}
