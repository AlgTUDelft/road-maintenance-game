/**
 * @file Dialog.java
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
 * @date         31 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.dialogs;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract dialog class for all popup-like dialogs. For dialogs that should
 * return a value, the {@link ObjectDialog} should be used.
 *
 * @author Joris Scharpff
 */
public abstract class Dialog extends DialogBox {
	/** The default button */
	private Button btnDefault;
	
	/** The cancel button */
	private Button btnCancel;
	
	/** The current active dialog */
	private static Dialog active;

	/**
	 * Creates a new dialog
	 * 
	 * @param title The dialog title
	 */
	// FIXME title is not set correctly
	public Dialog( String title ) {
		super( );
		
		initProvided( );
		
		setWidget( initUI( ) );
		setTitle( title );
		
		// setup dialog
		init( );
	}
	
	/**
	 * Sets the default button. The button click handler will be executed if the
	 * user presses the enter key
	 * 
	 * @param button The button to make default, null to clear
	 */
	protected void setDefault( Button button ) {
		btnDefault = button;
	}
	
	/**
	 * Sets the cancel button. The button click handler will be executed if the
	 * user presses the cancel key
	 * 
	 * @param button The button to set as cancel button, null to clear
	 */
	protected void setCancel( Button button ) {
		btnCancel = button;
	}
	
	/**
	 * Override keyboard native events to implement default and cancel buttons
	 * 
	 * @see com.google.gwt.user.client.ui.DialogBox#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
	 */
	@Override
  protected void onPreviewNativeEvent( NativePreviewEvent event ) {
    super.onPreviewNativeEvent( event );
    
    // check what event we are dealing with, only respond to key events
    switch( event.getTypeInt( ) ) {
      case Event.ONKEYDOWN:
    		final int key = event.getNativeEvent( ).getKeyCode( );
    	
    		switch( key ) {
    			case KeyCodes.KEY_ESCAPE:
    				if( btnCancel != null )	btnCancel.click( );
    				break;
    			
    			case KeyCodes.KEY_ENTER:
    				if( btnDefault != null ) btnDefault.click( );
    				break;
    		}
      break;
    }
  }
	
	/**
	 * Dialog boxed can override this function to initialise possible provided
	 * widgets
	 */
	protected void initProvided( ) {
		
	}
	
	/**
	 * Dialog boxes should override this function to return the correct UI
	 * implementation
	 * 
	 * @return The widget that is used as the dialog UI
	 */
	protected abstract Widget initUI( );
	
	/**
	 * Initialises the dialog, can be overridden for customisation. It is good
	 * practice to set default and cancel buttons here
	 */
	protected void init( ) {
		setGlassEnabled( true );		
	}

	/**
	 * @deprecated Use show( ) instead
	 * @see com.google.gwt.user.client.ui.PopupPanel#center()
	 */
	@Deprecated
	@Override
	public final void center( ) {
		show( );
	}
	
	/**
	 * Shows the dialog, calls onShow function
	 */
	@Override
	public void show( ) {
		// set this dialog as the active window
		active = this;
		
		// call function before showing to setup last details
		initShow( );
		
		// now show the panel
		super.show( );
		super.center( );
		
		// and call the onShow function
		onShow( );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.DialogBox#hide()
	 */
	@Override
	public void hide( ) {
		// unset active dialog
		active = null;
		
		super.hide( );
	}
	
	/**
	 * Called just before showing the dialog, can be overridden for last minute
	 * initialisation of the dialog.
	 */
	protected void initShow( ) {
		
	}
	
	/**
	 * Called when the dialog is shown, can be overridden for custom dialog
	 * initialisation
	 */
	protected void onShow( ) {
		
	}
	
	/**
	 * @return The current active dialog, null if none
	 */
	public static Dialog getActiveDialog( ) {
		return active;
	}
	
	/**
	 * Closes the active dialog, if any
	 * 
	 * @return The dialog that was closed, null if no dialog was running
	 */
	public static Dialog closeActiveDialog( ) {
		final Dialog opened = active;
		if( active != null ) {
			active.hide( );
		}
		
		return opened;
	}
}
