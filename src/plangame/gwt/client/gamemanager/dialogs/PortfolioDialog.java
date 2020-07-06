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
package plangame.gwt.client.gamemanager.dialogs;

import java.util.List;

import plangame.game.player.Player;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.model.tasks.Portfolio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PortfolioDialog extends ObjectDialog<Portfolio> {
	protected interface PortfolioDialogUIBinder extends UiBinder<Widget, PortfolioDialog> { }
	
	/** The player we are assigning */
	protected final Player player;
	
	/** The HTML components */
	@UiField protected DataListBox<Portfolio> lstPortfolios;
	@UiField protected Button btnCancel;
	@UiField protected Button btnOK;
	
	/**
	 * Creates the dialog
	 * 
	 * @param player The player to set the portfolio for
	 * @param portfolios The list of game portfolios
	 * @param handler The dialog handler
	 */
	public PortfolioDialog( Player player, List<Portfolio> portfolios, DialogHandler<Portfolio> handler ) {
		super( Lang.text.PortfolioDialog_Title( ), handler );
		
		this.player = player; 
		
		// add all
		lstPortfolios.clear( );
		lstPortfolios.addAll( portfolios );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((PortfolioDialogUIBinder)GWT.create( PortfolioDialogUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
		
		// done is the default button
		setDefault( btnOK );
		setCancel( btnCancel );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initShow()
	 */
	@Override
	protected void initShow( ) {
		// select current portfolio (if any)
		if( player.getPortfolio( ) != null )
			lstPortfolios.setSelectedItem( player.getPortfolio( ) );
	}
	
	/**
	 * Click handler for the OK button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnOK")
	protected void done( ClickEvent e ) {
		// check if there is a portfolio selected
		final Portfolio pf = lstPortfolios.getSelectedItem( );
		
		if( pf == null ) {
			ClientView.getInstance( ).notify( Lang.text.SelectPortfolio( ) );
			return;
		}
		
		// done
		OK( pf );
	}
	
	/**
	 * Cancel button click handler
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnCancel")
	protected void cancel( ClickEvent e ) {
		Cancel( );
	}
}
