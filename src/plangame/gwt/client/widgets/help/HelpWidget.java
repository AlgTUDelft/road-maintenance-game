/**
 * @file HelpWidget.java
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
 * @date         8 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.help;

import plangame.gwt.client.widgets.BasicWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Joris Scharpff
 */
public class HelpWidget extends BasicWidget {
	/** The UI binder for the interface */
	protected interface HelpWidgetUIBinder extends UiBinder<Widget, HelpWidget> {}
	protected final HelpWidgetUIBinder HelpWidgetUI = GWT.create( HelpWidgetUIBinder.class );
	
	/** The UI components */
	@UiField protected HTML divTitle;
	@UiField protected HTML divText;
	
	/**
	 * Creates a new help widget
	 */
	public HelpWidget( ) {
		super( );
		
		initWidget( HelpWidgetUI.createAndBindUi( this ) );
	}
	
	/**
	 * Sets the title
	 * 
	 * @param title The title
	 */
	public void setHelpTitle( String title ) {
		divTitle.setHTML( title );
	}
	
	/**
	 * Sets the displayed help text
	 * 
	 * @param text The help text
	 */
	public void setHelpText( String text ) {
		divText.setText( text );
	}
}
