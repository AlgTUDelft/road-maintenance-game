/**
 * @file SPWidget.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         12 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider;

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanChange.PlanChangeType;
import plangame.game.plans.PlanTask;
import plangame.game.player.PlanPreference;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.gameview.GameViewUI;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.serviceprovider.dialogs.EndGameDialog;
import plangame.gwt.client.serviceprovider.dialogs.MethodDialog;
import plangame.gwt.client.serviceprovider.dialogs.RequestSuggestionDialog;
import plangame.gwt.client.serviceprovider.dialogs.RequestSuggestionResult;
import plangame.gwt.client.serviceprovider.dialogs.StartGameDialog;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.controller.ControlWidget;
import plangame.gwt.client.widgets.controls.spoptions.OptionChangeListener;
import plangame.gwt.client.widgets.controls.spoptions.SPOptionsWidget;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.finance.FinanceWidget;
import plangame.gwt.client.widgets.help.HelpWidget;
import plangame.gwt.client.widgets.listeners.SelectionListener;
import plangame.gwt.client.widgets.map.MapWidget;
import plangame.gwt.client.widgets.plan.PlanWidget;
import plangame.gwt.client.widgets.scorebar.ScoreBarWidget;
import plangame.gwt.client.widgets.ttl.TTLTableWidget;
import plangame.gwt.client.widgets.ttltimeline.TTLTimelineWidget;
import plangame.gwt.shared.GameServerConfig;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.gameresponse.GameResults;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.tasks.Portfolio;
import plangame.model.tasks.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The interface code for the service provider
 *
 * @author Joris Scharpff
 */
public class ServiceProviderUI extends GameViewUI implements JointPlanUpdateListener {
	/** The UI binder for the interface */
	protected interface ServiceProviderUIBinder extends UiBinder<Widget, ServiceProviderUI> {}
	
	/** The selected PlannedTask */
	protected PlanTask selected;
	
	/** List of all controls that display based on the selected task */
	protected List<BasicWidget> selectwidgets;
		
	/** The UI elements */
	@UiField protected TabLayoutPanel tabMain;
	@UiField protected HorizontalPanel pnlTab;
	@UiField protected ScoreBarWidget wdScoreBar;
	@UiField protected PlanWidget wdPlan;
	@UiField protected ControlWidget wdController;
	@UiField protected MapWidget wdMap;
	@UiField protected FinanceWidget wdFinance;
	@UiField protected TTLTableWidget wdTTL;
	@UiField protected TTLTimelineWidget wdPlot;
	@UiField protected SPOptionsWidget wdOptions;
	@UiField protected HelpWidget wdHelp;
	
	/**
	 * Creates a new SPWidget
	 * 
	 * @param spview The service provider view owning the SPWidget
	 */
	public ServiceProviderUI( ServiceProvider spview ) {
		super( spview );
		
		// init update lists for widgets
		selectwidgets = new ArrayList<BasicWidget>( );
				
		// add selection handlers to all elements that can select methods
		addSelectionWidget( wdPlan );
		addSelectionWidget( wdMap );
		addSelectionWidget( wdFinance );
		addSelectionWidget( wdTTL );
		addSelectionWidget( wdPlot );
		
		// add an options change listener to the options widget
		wdOptions.addListener( new OptionChangeListener( ) {
			@Override public void onSetTTLRelative( boolean relative ) {
				// set relative TTL in all TTL widgets
				wdTTL.setDisplayRelative( relative );
				wdPlot.setDisplayRelative( relative );
				wdScoreBar.setDisplayRelative( relative );
			}
			
			@Override
			public void onSetTTLOnMap( boolean onmap ) {
				wdMap.setDisplayDistribution( onmap );
			}
		} );
		
		// set default options
		wdOptions.setTTLRelative( false );
		wdOptions.setTTLOnMap( true );
		
		pnlTab.setVisible( false );
	}
	
	/**
	 * @see plangame.gwt.client.ClientViewUI#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((ServiceProviderUIBinder) GWT.create( ServiceProviderUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#registerDataWidgets(plangame.gwt.client.gamedata.ClientGameData)
	 */
	@Override
	protected void registerDataWidgets( ClientGameData gamedata ) {
		gamedata.addUpdateListener( this );
		gamedata.addUpdateListener( wdMap );
		wdMap.setGameData( gamedata );
		gamedata.addUpdateListener( wdPlan );
		wdPlan.setGameData( gamedata );
		gamedata.addUpdateListener( wdFinance );
		wdFinance.setGameData( gamedata );
		gamedata.addUpdateListener( wdTTL );
		wdTTL.setGameData( gamedata );
		gamedata.addUpdateListener( wdPlot );
		wdPlot.setGameData( gamedata );
		gamedata.addUpdateListener( wdScoreBar );
		wdScoreBar.setGameData( gamedata );
		
		// manually set map & plot size
		wdMap.setSize( "250px", "250px" );
		wdPlot.setPixelSize( 480, 220 );		
	}
	
	/**
	 * Adds a widget to the list of widgets requiring selection information. Sets
	 * up an MethodSelectionListener in the widget to communicate back to the UI.
	 * 
	 * @param widget The widget to add
	 */
	private void addSelectionWidget( BasicWidget widget ) {
		// add it to the list of selection related widgets
		selectwidgets.add( widget );
		
		// add method listener to receive selection updates from the widget
		widget.addMethodSelectionListener( new SelectionListener( ) {
			@Override public void onSelectionChange( Object source, PlanTask prevsel, PlanTask newsel ) {
				setSelected( source, newsel );
			}
		} );
	}
		
	/**
	 * @return The currently selected task method
	 */
	public PlanTask getSelected( ) {
		return selected;
	}
	
	/**
	 * Sets the currently selected task, fires the selection change event.
	 * Current selection can be cleared by specifying null as parameter.
	 * 
	 * @param source The source object setting the selection
	 * @param ptask The plan task to be selected, null to unselect any method
	 */
	public void setSelected( Object source, PlanTask ptask ) {
		selected = ptask;
		
		// get TTL info
		if( getView( ).getGameData( ).isReady( ) && getView( ).getGameConfig( ).showTTLDistribution( ) ) {
			if( !wdMap.showTTLDistribution( ) ) {
				wdMap.clearTTLDistribution( );
			} else {
				if( ptask != null ) {
					wdMap.setTTLDistribution( getView( ).getGameData( ).getData( ).getTTLAvgNetworkDistribution( ptask ) );
				} else {
					wdMap.setTTLDistribution( getView( ).getGameData( ).getData( ).getTTLAvgNetworkDistribution( ) );
				}
			}
		}
		
		// fire selection change in all widgets that want to listen
		for( BasicWidget widget : selectwidgets )
			if( !widget.equals( source ) ) widget.setSelected( ptask, false );
	}
	
	/**
	 * Shows the add task dialog
	 * 
	 * @param player The player that wants to add a task
	 * @param gamedata The client game data
	 * @param dialoghandler The dialog callback handler
	 */
	protected void showAddTask( Player player, ClientGameData gamedata, DialogHandler<PlanChange> dialoghandler ) {
		//System.out.println( "Unplanned tasks for player: " + getClient( ).getPlayer( ).getID( ) );
		new MethodDialog( player, gamedata, wdOptions.isTTLRelative( ), dialoghandler ).show( );
	}
	
	/**
	 * Shows the edit task dialog
	 * 
	 * @param ptask The current planned method for the task, null if no method
	 * is planned
	 * @param gamedata The client game data
	 * @param stepmethod True starts the dialog in method mode, false in plan step  
	 * @param dialoghandler The dialog result handler 
	 */
	protected void showEditTask( PlanTask ptask, ClientGameData gamedata, boolean stepmethod, DialogHandler<PlanChange> dialoghandler ) {
		new MethodDialog( ptask, gamedata, stepmethod, wdOptions.isTTLRelative( ), dialoghandler ).show( );
	}
	
	/**
	 * Shows the request task suggestion dialog
	 * 
	 * @param tasks The available tasks
	 * @param task The selected task
	 * @param pref The current client plan preference
	 * @param handler The dialog result handler
	 */
	protected void showRequestSuggestion( List<Task> tasks, Task task, PlanPreference pref, DialogHandler<RequestSuggestionResult> handler ) {
		new RequestSuggestionDialog( tasks, task, pref, handler ).show( );
	}

	/**
	 * Shows the request plan suggestion dialog
	 * 
	 * @param pref The current client plan preferences
	 * @param handler The result handler
	 */
	protected void showRequestSuggestion( PlanPreference pref, DialogHandler<RequestSuggestionResult> handler ) {
		new RequestSuggestionDialog( pref, handler ).show( );
	}
	
	/**
	 * Shows the start game dialog
	 * 
	 * @param player The player to show the dialog for
	 * @param gamedata The game data
	 */
	protected void showStartDialog( Player player, ClientGameData gamedata ) {
		new StartGameDialog( player, gamedata ).show( );
	}
		
	/**
	 * Shows the end game dialog
	 * 
	 * @param player The player to show the dialog for
	 * @param gamedata The game data
	 */
	protected void showEndDialog( Player player, ClientGameData gamedata ) {
		new EndGameDialog( player, gamedata, wdOptions.isTTLRelative( ) ).show( );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#updateServerInfo(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	protected void updateServerInfo( GameServerInfo info ) {
		// new server info is known, update config dependent settings
		final GameServerConfig config = info.getConfig( );
		wdPlan.setShowOtherMethodNames( !config.hideMethodNames( ) );		
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		// deselect current method
		final PlanTask selected = getSelected( );
		if( selected != null )
			setSelected( this, null );
			
		// check the joint plan if it still contains the method and select it
		if( selected != null && jplan.isPlanned( selected.getTask( ) ) ) {
			for( PlanTask pt : jplan.getPlanned( ) )
				if( selected.equalsMethod( pt ) )
					setSelected( this, pt );
		}
	}
	
	/**
	 * Updates all client related widgets
	 * 
	 * @param client The new client
	 */
	protected void updateClient( SPClient client ) {
		// get the player
		final Player p = client.getPlayer( );
		if( p == null ) return;
		
		// set the player in the widgets
		wdPlan.setPlayer( p );
		wdScoreBar.setPlayer( p );
		wdPlot.setPlayer( p );
		wdFinance.setPlayer( p );
		wdTTL.setPlayer( p );
		
		// update portfolio widgets
		final Portfolio pf = p.getPortfolio( );
		if( pf == null ) return;
		wdFinance.setTasks( pf.getTasks( ) );
		wdTTL.setTasks( pf.getTasks( ) );
	}
	
	/**
	 * Updates the UI when the client is restored
	 */
	protected void updateClientRestore( ) {
		// clear selection
		setSelected( this, null );
		
		// draw TTL network plots if any task by other players is known
		wdPlot.setDrawNetwork( getView( ).getGameData( ).getJointPlan( ).anyOtherPlanned( getView( ).getPlayer( ) ) );
	}
	
	/**
	 * Updates the UI to start the game
	 */
	protected void updateStartGame( ) {		
		// disable drawing of network TTL
		wdPlot.setDrawNetwork( false );

		// make sure nothing is selected
		setSelected( this, null );
		
		// show the tables
		pnlTab.setVisible( true );
	}
	
	/**
	 * Updates the UI when a plan acceptance phase starts
	 */
	protected void updateAccepting( ) {
		// enable drawing of network TTL plots (if not already done by user)
		wdPlot.setDrawNetwork( true );
	}
	
	/**
	 * Updates all client state related info
	 * 
	 * @param oldstate The previous state
	 * @param newstate The new client state
	 */
	protected void updateClientState( ClientState oldstate, ClientState newstate ) {
		// notify controller of state change
		wdController.setState( newstate );
		
		// update help message
		updateHelp( newstate );
		
		// make the plan editable if the state is planning
		wdPlan.setEditable( newstate == ClientState.InPlanning );
		
		// update style of execution marker if required
		wdPlan.setExecuting( newstate == ClientState.Executing );
		
		// show the table
		pnlTab.setVisible( true );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// if the selected method has changed, re-select it
		if( getSelected( ) == null && change.getType( ) != PlanChangeType.MethodAdded ) return;
		
		// update selection based on type
		switch( change.getType( ) ) {
			case MethodAdded:
				setSelected( this, change.getResult( ) );	
				break;

			case MethodChanged:
			case MethodMoved:
			case MethodDelaySet:
				// re-select the planned task if required
				if( getSelected( ).equalsMethod( change.getPrevious( ) ) )
					setSelected( this, change.getResult( ) );
				break;

			case MethodRemoved:
				if( getSelected( ).equals( change.getPrevious( ) ) )
					setSelected( this, null );
				break;
		}
	}
	
	/**
	 * Updates the UI when new execution results come in
	 * 
	 * @param results The game results
	 */
	protected void updateResults( GameResults results ) {
		// update game time marker
		wdPlan.updateWeekMarker( );
	}
	
	/**
	 * Updates the help message based on the new state
	 * 
	 * @param state The new client state
	 */
	protected void updateHelp( ClientState state ) {
		// set help title and text using resource bundle
		final String title;
		switch( state ) {
			case Initialising: title = Lang.text.SP_StateInitialisingTitle( ); break;
			case AwaitingPortfolio: title = Lang.text.SP_StateAwaitingPortfolioTitle( ); break;
			case WaitingToStart: title = Lang.text.SP_StateWaitingToStartTitle( ); break;
			case Idle: title = Lang.text.SP_StateIdleTitle( ); break;
			case InPlanning: title = Lang.text.SP_StateInPlanningTitle( ); break;
			case Submitted: title = Lang.text.SP_StateSubmittedTitle( ); break;
			case Accepting: title = Lang.text.SP_StateAcceptingTitle( ); break;
			case Accepted: title = Lang.text.SP_StateAcceptedTitle( ); break;
			case Declined: title = Lang.text.SP_StateDeclinedTitle( ); break;
			case Executing: title = Lang.text.SP_StateExecutingTitle( ); break;
			case Finished: title = Lang.text.SP_StateFinishedTitle( ); break;
			case Reconnecting: title = Lang.text.SP_StateReconnectingTitle( ); break;
			case Disconnected: title = Lang.text.SP_StateDisconnectedTitle( ); break;
			
			default:
				assert false : "Unknown state!";
			title = "";
		}
		wdHelp.setHelpTitle( title );

		// set help title and text using resource bundle
		final String text;
		switch( state ) {
			case Initialising: text = Lang.text.SP_StateInitialising( ); break;
			case AwaitingPortfolio: text = Lang.text.SP_StateAwaitingPortfolio( ); break;
			case WaitingToStart: text = Lang.text.SP_StateWaitingToStart( ); break;
			case Idle: text = Lang.text.SP_StateIdle( ); break;
			case InPlanning: text = Lang.text.SP_StateInPlanning( ); break;
			case Submitted: text = Lang.text.SP_StateSubmitted( ); break;
			case Accepting: text = Lang.text.SP_StateAccepting( ); break;
			case Accepted: text = Lang.text.SP_StateAccepted( ); break;
			case Declined: text = Lang.text.SP_StateDeclined( ); break;
			case Executing: text = Lang.text.SP_StateExecuting( ); break;
			case Finished: text = Lang.text.SP_StateFinished( ); break;
			case Reconnecting: text = Lang.text.SP_StateReconnecting( ); break;
			case Disconnected: text = Lang.text.SP_StateDisconnected( ); break;
			
			default:
				assert false : "Unknown state!";
				text = "";
		}
		wdHelp.setHelpText( text );		
	}

	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#getView()
	 */
	@Override
	protected ServiceProvider getView( ) {
		return (ServiceProvider)super.getView( );
	}
}
