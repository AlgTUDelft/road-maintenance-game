/**
 * @file GameManager.java
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
 * @date         14 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamemanager;

import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.game.plans.PlanTask;
import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.gamemanager.dialogs.DelayDialogResult;
import plangame.gwt.client.gameview.GameView;
import plangame.gwt.client.resource.GMResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.client.util.VoidRPCCallback;
import plangame.gwt.client.widgets.controller.ControlButton;
import plangame.gwt.client.widgets.controller.ControlWidget;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.events.AcceptEvent;
import plangame.gwt.shared.events.AcceptedEvent;
import plangame.gwt.shared.events.AssignPortfolioEvent;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.ExecutedEvent;
import plangame.gwt.shared.events.JoinEvent;
import plangame.gwt.shared.events.ProcessPendingEvent;
import plangame.gwt.shared.events.UpdateClientStateEvent;
import plangame.gwt.shared.events.UpdateStateEvent;
import plangame.gwt.shared.exceptions.InvalidPlanException;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Portfolio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.Window;

/**
 * The game manager GWT client
 * 
 * @author Joris Scharpff
 */
public class GameManager extends GameView {
	/** The current known game state */
	protected GameState gamestate;	
	
	/** Current known clients and their states */
	protected ObjectMap<SPClient, ClientState> clients;
	
	/** The default value for the execution sleep parameter */
	private final static long DEFAULT_SLEEP = 500;
	
	/**
	 * Creates a new GameManager with a RPC proxy for server calls
	 */
	public GameManager( ) {
		super( (ClientRPCAsync)GWT.create( GMRPC.class ), (GMResource)GWT.create( GMResource.class ) );
		
		// set the game state to initialising
		setGameState( GameState.Initialising );		
		
		// initialise an empty client map
		clients = new ObjectMap<SPClient, ClientState>( );
		
		// initialise controller
		initController( getUI( ).wdController );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#initResource()
	 */
	@Override
	protected void initResource( ) {
		super.initResource( );
		
		getResources( ).gmcss( ).ensureInjected( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getResources()
	 */
	@Override
	@UiFactory
	public GMResource getResources( ) {
		return (GMResource)super.getResources( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getClientType()
	 */
	@Override
	public ClientType getClientType( ) {
		return ClientType.GameManager;
	}

	/**
	 * @see plangame.gwt.client.gameview.GameView#getRPC()
	 */
	@Override
	protected GMRPCAsync getRPC( ) {
		return (GMRPCAsync)super.getRPC( );
	}

	/**
	 * @see plangame.gwt.client.gameview.GameView#initUI()
	 */
	@Override
	protected ClientViewUI initUI( ) {
		return new GameManagerUI( this );
	}

	/**
	 * @see plangame.gwt.client.gameview.GameView#getUI()
	 */
	@Override
	public GameManagerUI getUI( ) {
		return (GameManagerUI)super.getUI( );
	}
	
	/**
	 * Sets the actions in the controller
	 * 
	 * @param controller The controller
	 */
	protected void initController( ControlWidget controller ) {
		// shows the assign portfolio window
		controller.addControl( new ControlButton( Lang.text.GM_AssignPortfolio( ), GameState.Initialising ) {
			@Override protected void onClick( ) {
				// check if a player is selected
				final SPClient client = getUI( ).wdPlayers.getData( ).getSelectedItem( );
				if( client == null ) {
					GameManager.this.notify( Lang.text.SelectPlayer( ) );
					return;
				}
				
				getRPC( ).getPortfolios( getClient( ).getID( ), new RPCCallback<List<Portfolio>>( Lang.text.GM_RetrievingPortfolios( ) ) {
					@Override
					public void success( List<Portfolio> portfolios ) {
						getUI( ).showAssignDialog( client, portfolios, new DialogHandler<Portfolio>( ) {
							@Override
							public void OK( Portfolio item ) {
								// check if the same
								if( item.equals( client.getPlayer( ).getPortfolio( ) ) ) return;
								
								// try and assign the portfolio
								assignPortfolio( client, item );
							}
						} );
					}

					@Override protected String getFailureText( ) { return Lang.text.GM_RetrievePortfoliosFailed( ); }
				} );
			}
		} );
		
		// start the game
		controller.addControl( new ControlButton( Lang.text.GM_StartGame( ), GameState.Initialising ) {
			@Override protected void onClick( ) {
				getRPC( ).startGame( getClient( ).getID( ),
						new VoidRPCCallback( Lang.text.GM_StartingGame( ), Lang.text.GM_StartGameFail( ) ) );
			}
		} );
		
		// start plan round
		controller.addControl( new ControlButton( Lang.text.GM_StartPlanning( ), GameState.Idle ) {
			@Override protected void onClick( ) {
				getRPC( ).startPlanRound( getClient( ).getID( ),
						new VoidRPCCallback( Lang.text.GM_StartPlanningFail( ) ) );
			}
		} );
		
		// start execution
		controller.addControl( new ControlButton( Lang.text.GM_StartExecution( ), GameState.Idle ) {
			@Override protected void onClick() {
				startExecution( );
			}
		} );
		
		// stop execution (effective next round)
		controller.addControl( new ControlButton( Lang.text.GM_StopExecute( ), GameState.Executing ) {
			@Override protected void onClick( ) {
				stopExecution( );
			}
		} );
		
		// allows the GM to change the game state
		controller.addControl( new ControlButton( Lang.text.GM_ChangeGameState( ) ) {
			@Override
			protected void onClick( ) {
				getUI( ).showChangeGameState( getGameState( ), new DialogHandler<GameState>( ) {
					@Override public void OK( GameState item ) {
						forceGameState( item );
					}
				} ); 
			}
		} );
		
		// allows the GM to change the player's state
		controller.addControl( new ControlButton( Lang.text.GM_ChangeClientState( ) ) {
			@Override protected void onClick( ) {
				// check if there is a client selected
				final SPClient client = checkSelected( );
				if( client == null ) return;
				
				// show change state dialog
				getUI( ).showChangeClientState( client, getClientState( client ), new DialogHandler<ClientState>( ) {
					@Override public void OK( ClientState item ) {
						setClientState( client, item );
					}
				} );
			}
		} );
		
		// add disconnect button
		controller.addDisconnect( );
		
		// set initial state
		controller.setState( getGameState() );
	}
	
	/**
	 * Returns the selected client, shows a notification if no client is
	 * selected.
	 * 
	 * @return The client or null if no client is selected
	 */
	private SPClient checkSelected( ) { 
		// check if there is a client selected
		final SPClient client = getUI( ).wdPlayers.getData( ).getSelectedItem( );
		if( client == null ) {
			GameManager.this.notify( Lang.text.SelectPlayer( ) );
			return null;
		}
		
		return client;
	}
	
	/**
	 * Assigns a portfolio to the client
	 * 
	 * @param client The client
	 * @param portfolio The portfolio to assign
	 */
	private void assignPortfolio( SPClient client, Portfolio portfolio ) {
		getRPC( ).assignPortfolio( client.getID( ), portfolio.getID( ),
				new VoidRPCCallback( Lang.text.GM_AssigningPortfolio( ), Lang.text.GM_AssignPortfolioFail( ) ) );
	}
	
	/**
	 * @param client The client
	 * @return The currently known client state for
	 */
	public ClientState getClientState( SPClient client ) {
		return clients.get( client );
	}
	
	/**
	 * Sets the game state, useful when the game ended up in an illegal state
	 * 
	 * @param state The new game state
	 */
	private void forceGameState( GameState state ) {
		// only send if the state changes
		if( getGameState( ).equals( state ) ) return;
		
		// change the state by sending a message to the game server
		getRPC( ).setGameState( getClient( ).getID( ), state, new RPCCallback<GameState>( Lang.text.GM_ChangingGameState( ) ) {
			@Override
			public void success( GameState result ) {
				// success, nothing to do the state will be updated through an event
			}

			@Override protected String getFailureText( ) { return Lang.text.GM_ChangeGameStateFail( ); }						
		} );
	}
	
	/**
	 * Sets the state of a client, useful when the client ended up in an illegal
	 * state somehow
	 * 
	 * @param client The client
	 * @param state The new client state
	 */
	private void setClientState( SPClient client, ClientState state ) {		
		// only send if the state changes
		if( getClientState( client ).equals( state ) ) return;
		
		// changing the client status
		getRPC( ).setClientState( client.getID( ), state, new RPCCallback<ClientState>( Lang.text.GM_ChangingClientState( ) ) {
			@Override
			public void success( ClientState result ) {
				// success, nothing to do the state will be updated through an event
			}

			@Override protected String getFailureText( ) { return Lang.text.GM_ChangeClientStateFail( ); }			
		} );
	}
	
	/**
	 * Setup plan execution
	 */
	private void startExecution( ) {
		// show dialog to setup execution
		getUI( ).showExecuteDialog( DEFAULT_SLEEP, new DialogHandler<ExecutionInfo>( ) {
			@Override public void OK( ExecutionInfo item ) {
				getRPC( ).startExecute( getClient( ).getID( ), item,
					new VoidRPCCallback( /* Lang.text.GM_ExecutingPlan( ), */ Lang.text.GM_ExecutePlanFail( ) ) {
					/**
					 * Handle the plan invalid exception specifically
					 * @see plangame.gwt.client.util.RPCCallback#handleException(java.lang.Throwable)
					 */
					@Override public boolean handleException( Throwable caught ) {
							if( caught instanceof InvalidPlanException ) {
								// notify of errors
								GameManager.this.notify( ClientUtil.getLocalisedPlanErrors( Lang.text.GM_ExecutePlanFail( ), ((InvalidPlanException)caught).getErrors( ) ) );
								return true;
							}
							
							return false;
						}
					}
				);
			}
		} );
	}
	
	/**
	 * Signals the game server to stop execution, will be effective from the
	 * moment the game receives the stop request. Any week that is currently
	 * under execution will be finished.
	 */
	private void stopExecution( ) {
		getRPC( ).stopExecute( getClient( ).getID( ), new VoidRPCCallback( Lang.text.GM_StoppingExecution( ), Lang.text.GM_StopExecutionFail( ) ) );
	}
		
	/**
	 * Processes the results of one plan step, asks the game manager to delay
	 * pending tasks. Calls submit results after processing
	 * 
	 * @param results The plan step results
	 */
	private void processStepResults( final PlanStepResult results ) {
		// should we delay tasks?
		if( results != null && results.getPending( ).size( ) > 0 ) {
			getUI( ).showDelayDialog( results, new DialogHandler<DelayDialogResult>( ) {
				/**
				 * @see plangame.gwt.client.widgets.dialogs.DialogHandler#OK(java.lang.Object)
				 */
				@Override
				public void OK( DelayDialogResult item ) {
					// process results
					for( PlanTask pt : item.getDelayed( ) )
						results.delayTask( pt, true );
					for( PlanTask pt : item.getCompleted( ) )
						results.delayTask( pt, false );
					
					// submit the results to the server
					submitResults( results, !item.execContinue( ) );
				}
			} );
		} else {
			submitResults( results, false );
		}
	}
	
	/**
	 * Submits the processed results to the game server
	 * 
	 * @param results The processed step results
	 * @param stop True to stop execution after handling
	 */
	private void submitResults( PlanStepResult results, boolean stop ) {
		getRPC( ).submitPending( getClient( ).getID( ), results, stop, 
			new VoidRPCCallback( Lang.text.GM_SubmittingPending( ), Lang.text.GM_SubmitPendingFail( ) ) {
				/**
				 * Handle the plan invalid exception specifically
				 * @see plangame.gwt.client.util.RPCCallback#handleException(java.lang.Throwable)
				 */
				@Override public boolean handleException( Throwable caught ) {
					if( caught instanceof InvalidPlanException ) {
						// notify of errors
						GameManager.this.notify( ClientUtil.getLocalisedPlanErrors( Lang.text.GM_ExecutePlanFail( ), ((InvalidPlanException)caught).getErrors( ) ) );
						return true;
					}
					
					return false;
				}
			} );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameView#onEvent(plangame.gwt.shared.events.Event)
	 */
	@Override
	protected boolean onEvent( Event event ) {
		if( event instanceof AssignPortfolioEvent ) {
			// player is assigned a (new) portfolio
			final AssignPortfolioEvent ae = (AssignPortfolioEvent) event;
			updateClient( ae.getClient( ) );
			return true;			
		} else if( event instanceof JoinEvent ) {
			// player joined the game
			final JoinEvent je = (JoinEvent) event;
			addClient( (SPClient) je.getClient( ) );
			return true;
		} else if( event instanceof ProcessPendingEvent ) {
			// one or more steps have been executed but now methods are pending delay
			processStepResults( ((ProcessPendingEvent)event).getResults( ) );
			return true;
		} else if( event instanceof ExecutedEvent ) {
			// FIXME process execution results			
			return true;
		} else if( event instanceof AcceptEvent ) {
			// the players have made their plans, they are now deciding on whether to
			// accept it or not
			getGameData( ).setJointPlan( ((AcceptEvent)event).getJointPlan( ) );
			return true;
		} else if( event instanceof AcceptedEvent ) {
			// nothing to do now, game state will be updated
			return true;
		} else if( event instanceof UpdateStateEvent ) {
			// the server is updating to a new game state
			setGameState( ((UpdateStateEvent) event).getGameState( ) );
			return true;
		} else if( event instanceof UpdateClientStateEvent ) {
			updateClientStates( ((UpdateClientStateEvent)event).getStates( ) );
			return true;
		}
		
		return super.onEvent( event );
	}
		
	/**
	 * Sets the current game state for the client and updates the interface
	 * accordingly. Calls the onStateChange function afterwards for client
	 * specific implementations
	 * 
	 * @param state The game state
	 */
	protected void setGameState( GameState state ) {
		// store the game state
		gamestate = state;
		log( LogType.Verbose, "Changed state to '" + state.toString( ) + "'" );
		
		// notify controller of state change
		getUI( ).wdController.setState( state );
		
		// update help message
		getUI( ).updateHelp( state );
	}
	
	/**
	 * Updates a client
	 * 
	 * @param client The client to update
	 */
	protected void updateClient( SPClient client ) {
		// replace the client object in the map
		final ClientState prevstate = getClientState( client );
		clients.remove( client );
		clients.put( client, prevstate );
		
		getUI( ).updateClient( client );
	}
	
	/**
	 * Updates the client states to match the new map
	 * 
	 * @param states The new states map
	 */
	protected void updateClientStates( ObjectMap<SPClient, ClientState> states ) {
		// set all client statuses described by the event
		for( SPClient c : states.getKeys( ) ) {
			clients.put( c, states.get( c ) );
		}
		
		// refresh the list
		getUI( ).wdPlayers.getData( ).refresh( );
	}

	
	/**
	 * Checks if the game is in the specified state
	 * 
	 * @param state The game state
	 * @return True if the current state matches the given state
	 */
	public boolean inState( GameState state ) {
		return gamestate == state;
	}
	
	/**
	 * Retrieves the current game state
	 * 
	 * @return The state the view is currently in
	 */
	protected GameState getGameState( ) {
		return gamestate;
	}

	/**
	 * Adds a client to this game's client list
	 * 
	 * @param client The client to add
	 */
	protected void addClient( SPClient client ) {
		clients.put( client, ClientState.Initialising );
		getUI( ).addClient( client );
	}
	
	/**
	 * @see plangame.gwt.client.ClienViewUpdates#onClientChange(plangame.gwt.shared.clients.Client, plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void onClientChange( Client oldclient, Client newclient ) {
		// the client object has changed
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerStateChange(plangame.gwt.shared.state.GameServerState, plangame.gwt.shared.state.GameServerState)
	 */
	@Override
	public void onServerStateChange( GameServerState oldstate, GameServerState newstate ) {
		// set the state of the game
		setGameState( newstate.getGameState( ) );
		
		// add all the clients
		for( SPClient client : newstate.getClientStates( ).getKeys( ) )
			addClient( client );
		
		// set the client states
		updateClientStates( newstate.getClientStates( ) );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerInfoChange(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	public void onServerInfoChange( GameServerInfo serverinfo ) {
		// update window title if required
		Window.setTitle( "[" + Lang.text.GM_WindowTitle( ) + "]" + (serverinfo != null ? " " + serverinfo.getName( ) : "") );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onClientRestore(plangame.gwt.shared.gameresponse.RestoreResponse)
	 */
	@Override
	public void onClientRestore( RestoreResponse restore ) {
		// check if there are currently execution results pending
		if( restore.getExecutionInfo( ) != null ) {
			processStepResults( restore.getExecutionInfo( ).getPendingResults( ) );
		}
	}

	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onStartGame()
	 */
	@Override
	public void onStartGame( ) {
		// do nothing UI is updated automatically
	}
}
