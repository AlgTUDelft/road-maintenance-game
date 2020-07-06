/**
 * @file TaskWidget.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         19 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.servermanager.dialogs;

import java.util.List;

import plangame.game.player.Player;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.model.tasks.Portfolio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PlayerJoinDialog extends ObjectDialog<Portfolio> {
	protected interface PlayerJoinDialogUIBinder extends UiBinder<Widget, PlayerJoinDialog> { }
		
	/** The portfolios */
	protected List<Portfolio> portfolios;
	
	/** The HTML components */
	@UiField protected Label lblPlayerName;
	@UiField protected ListBox lstPortfolio;
	@UiField protected Button btnOK;
	@UiField protected Button btnCancel;
	
	/**
	 * Creates the basic dialog
	 * 
	 * @param player The player
	 * @param portfolios The available portfolios
	 * @param handler The dialog handler
	 */
	public PlayerJoinDialog( Player player, List<Portfolio> portfolios, DialogHandler<Portfolio> handler ) {
		super( "Assign portfolio", handler );

		this.portfolios = portfolios;

		lblPlayerName.setText( player.toString( ) );
		lstPortfolio.clear( );
		for( Portfolio p : portfolios )
			lstPortfolio.addItem( p.toString( ) );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((PlayerJoinDialogUIBinder)GWT.create( PlayerJoinDialogUIBinder.class )).createAndBindUi( this );
	}

	/**
	 * Click handler for the OK button
	 * 
	 * @param e
	 */
	@UiHandler("btnOK")
	protected void OK( ClickEvent e ) {
		// check if a name has been specified
		if( lstPortfolio.getSelectedIndex( ) == -1 ) {
			ClientView.getInstance( ).warning( "Please choose a portfolio to assign!" );
			return;
		}

		// return the selected method
		OK( portfolios.get( lstPortfolio.getSelectedIndex( ) ) );
	}
	
	/**
	 * Click handler for the cancel button
	 * @param e
	 */
	@UiHandler("btnCancel")
	protected void Cancel( ClickEvent e ) {
		Cancel( );
	}
}
