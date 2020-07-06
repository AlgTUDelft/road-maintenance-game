/**
 * @file AssetManager.java
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
 * @date         9 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider;

import java.util.List;

import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanChange.PlanChangeType;
import plangame.game.plans.PlanError;
import plangame.game.plans.PlanException;
import plangame.game.plans.PlanTask;
import plangame.game.player.PlanPreference;
import plangame.game.player.Player;
import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.gameview.GameView;
import plangame.gwt.client.resource.SPResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.serviceprovider.dialogs.RequestSuggestionResult;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.client.widgets.controller.ControlButton;
import plangame.gwt.client.widgets.controller.ControlWidget;
import plangame.gwt.client.widgets.dialogs.Dialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.events.AcceptEvent;
import plangame.gwt.shared.events.AcceptedEvent;
import plangame.gwt.shared.events.AssignPortfolioEvent;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.ExecutedEvent;
import plangame.gwt.shared.events.PlanEvent;
import plangame.gwt.shared.events.ReassignEvent;
import plangame.gwt.shared.events.UpdateClientStateEvent;
import plangame.gwt.shared.gameresponse.GameResults;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.tasks.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.Window;


/**
 * @author Joris Scharpff
 */
public class ServiceProvider extends GameView {
	/** The state this client is currently in */
	protected ClientState state;
	
	/**
	 * Constructs a Service Provider game view, also instantiates a RPC proxy
	 * to the server
	 */
	public ServiceProvider( ) {
		super( (ClientRPCAsync)GWT.create( SPRPC.class ), (SPResource)GWT.create( SPResource.class ) );
		
		// set the instance and initial state
		state = ClientState.Initialising;
		
		// setup controller
		initController( getUI( ).wdController );
	}
	
	/**
	 * @return The current Service Provider view that is running on the browser
	 */
	public static ServiceProvider getInstance( ) {
		assert (instance != null) : "No service provider client instance is running (yet)";
		return (ServiceProvider)instance;
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getResources()
	 */
	@Override
	@UiFactory
	public SPResource getResources( ) {
		return (SPResource)super.getResources( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#initResource()
	 */
	@Override
	protected void initResource( ) {
		super.initResource( );
		
		// init SP specific CSS
		getResources( ).spcss( ).ensureInjected( );
		getResources( ).requestdialogcss( ).ensureInjected( );
		getResources( ).methoddialogcss( ).ensureInjected( );
		getResources( ).startdialogcss( ).ensureInjected( );
		getResources( ).endgamedialogcss( ).ensureInjected( );
		getResources( ).financecss( ).ensureInjected( );
		getResources( ).ttlcss( ).ensureInjected( );
		getResources( ).mechcss( ).ensureInjected( );
		getResources( ).plotcss( ).ensureInjected( );
	}
	
	/**
	 * Initialises the user interface for the service provider
	 * 
	 * @see plangame.gwt.client.view.GameView#initUI()
	 */
	@Override
	protected ClientViewUI initUI( ) {
		return new ServiceProviderUI( this );
	}
	
	/**
	 * Initialises the controller used
	 * 
	 * @see plangame.gwt.client.view.GameView#initController()
	 */
	protected void initController( ControlWidget controller ) {
		// Add task method to plan
		controller.addControl( new ControlButton( Lang.text.SP_AddMethod( ) , ClientState.InPlanning) {
			@Override
			protected void onClick( ) {
				// check the number of tasks left
				final List<Task> tasks = getGameData( ).getJointPlan( ).getUnplannedTasks( getPlayer( ) );
				if( tasks.size( ) == 0 ) {
					ServiceProvider.this.notify( Lang.text.SP_AllTasksPlanned( ) );
					return;
				}
				
				// show choose task dialog to determine what task to add
				getUI( ).showAddTask( getPlayer( ), getGameData( ), new DialogHandler<PlanChange>( ) {
					@Override
					public void OK( PlanChange item ) {
						// apply change if any
						if( item != null ) {
							applyChange( item, false );
						}
					}
				} );
			}
		} );
		
		// changes the methods for the task, starts in method step
		controller.addControl( new ControlButton( Lang.text.SP_ChangeMethod( ), ClientState.InPlanning ) {
			@Override protected void onClick( ) { showEditMethod( true ); }
		} );
		
		// changes the methods for the task, starts in plan step
		controller.addControl( new ControlButton( Lang.text.SP_MoveMethod( ), ClientState.InPlanning ) {
			@Override protected void onClick( ) { showEditMethod( false ); }
		} );		
		
		// remove a task from the plan
		controller.addControl( new ControlButton( Lang.text.SP_RemoveMethod( ), ClientState.InPlanning ) {
			@Override
			protected void onClick( ) {
				// check if a method is selected
				if( getUI( ).getSelected( ) == null ) {
					ServiceProvider.this.notify( Lang.text.NoMethodSelected( ) );
					return;
				}

				// get the method
				final PlanTask ptask = getUI( ).getSelected( );				

				// check if this task is one of the player's tasks
				if( !ptask.getPlayer( ).equals( getPlayer( ) ) ) {
					ServiceProvider.this.notify( Lang.text.SP_MethodRemoveNotMine( ptask.toString( ) ) );
					return;
				}
								
				// check if the player is allowed to edit this method
				if( getGameData( ).getJointPlan( ).isExecuting( ptask ) ) {
					ServiceProvider.this.notify( Lang.text.SP_MethodRemoveExecuting( ptask.toString( ) ) );
					return;
				}
				
				// get the method and remove it
				applyChange( PlanChange.remove( ptask ), true );
			}
		} );
		
		// submits the current plan
		controller.addControl( new ControlButton( Lang.text.SP_SubmitPlan( ), ClientState.InPlanning ) {		
			@Override
			protected void onClick( ) {
				submitPlan( );
			}
		} );
		
		// requests a suggestion for the method
		controller.addControl( new ControlButton( Lang.text.SP_RequestTaskSuggestion( ), ClientState.InPlanning ) {			
			@Override protected void onClick( ) {
				getTaskSuggestion( );
			}
			
			// only display when suggestions are allowed
			@Override public boolean shouldDisplay( ) {
				if( getGameConfig( ) == null )
					return false;
				
				return getGameConfig( ).allowTaskSuggestions( );
			}
		} );
		
		// requests a suggestion from the automated planner
		// FIXME make configurable
		/*
		controller.addControl( new ControlButton( Lang.text.SP_RequestPlanSuggestion( ), ClientState.InPlanning ) {
			@Override
			protected void onClick( ) {
				getPlanSuggestion( );
			}
		} );
		*/

		// accepts the joint plan
		controller.addControl( new ControlButton( Lang.text.SP_AcceptPlan( ), ClientState.Accepting ) {
			@Override
			protected void onClick( ) {
				acceptPlan( true );
			}
		} );
		
		// declines the joint plan
		controller.addControl( new ControlButton( Lang.text.SP_DeclinePlan( ), ClientState.Accepting ) {
			@Override
			protected void onClick( ) {
				acceptPlan( false );
			}
		} );
		
		// disconnect from server
		// TODO make configurable
		// controller.addDisconnect( );

		// and set the initial state
		controller.setState( state );
	}
	
	/** 
	 * @return The current client
	 */
	@Override
	public SPClient getClient( ) {
		return (SPClient)super.getClient( );
	}
	
	/**
	 * Retrieves a method suggestion from the server
	 */
	private void getTaskSuggestion( ) {
		// get the method
		final PlanTask ptask = getUI( ).getSelected( );				

		// check if this task is one of the player's tasks
		if( ptask != null ) {
			if( !ptask.getPlayer( ).equals( getPlayer( ) ) ) {
				ServiceProvider.this.notify( Lang.text.SP_MethodSuggestNotMine( ptask.toString( ) ) );
				return;
			}
							
			// check if the player is allowed to edit this method
			if( getGameData( ).getJointPlan( ).isExecuting( ptask ) ) {
				ServiceProvider.this.notify( Lang.text.SP_MethodSuggestExecuting( ptask.toString( ) ) );
				return;
			}
		}
		getUI( ).showRequestSuggestion( getPlayer( ).getPortfolio( ).getTasks( ), (ptask != null ? ptask.getTask( ) : null), new PlanPreference( ), new DialogHandler<RequestSuggestionResult>( ) {
			/**
			 * @see plangame.gwt.client.widgets.dialogs.DialogHandler#OK(java.lang.Object)
			 */
			@Override
			public void OK( RequestSuggestionResult item ) {
				getSuggestion( item.task, item.prefs );
			}
		} );		
	}
	
	/**
	 * Retrieves a plan suggestion from the server
	 */
	// currently unused
	@SuppressWarnings("unused")
	private void getPlanSuggestion( ) {
		getUI( ).showRequestSuggestion( new PlanPreference( ), new DialogHandler<RequestSuggestionResult>( ) {
			/**
			 * @see plangame.gwt.client.widgets.dialogs.DialogHandler#OK(java.lang.Object)
			 */
			@Override
			public void OK( RequestSuggestionResult item ) {
				getSuggestion( null, item.prefs );
			}
		} );		
	}
	
	/**
	 * Requests a suggestion from the server
	 * 
	 * @param task The task (null for plan suggestion)
	 * @param prefs The preferences
	 */
	private void getSuggestion( final Task task, PlanPreference prefs ) {
		getRPC( ).getSuggestion( getClient( ).getID( ), getGameData( ).getJointPlan( ), task, prefs, new RPCCallback<Plan>( Lang.text.SP_RequestingSuggestion( ) ) {
			@Override public void success( Plan result ) {
				// update my plan
				getGameData( ).setPlan( result );
				
				// select task
				if( task != null ) {
					final PlanTask pt = getGameData( ).getJointPlan( ).getPlanned( task );
					if( pt != null )
						getUI( ).setSelected( this, pt );
					else
						ServiceProvider.this.notify( Lang.text.RequestSuggestionDialog_TaskSuggestionFailed( ) );
				}
			}

			@Override protected String getFailureText( ) { return Lang.text.SP_RequestSuggestionFail( ); }
		} );		
	}
	
	/**
	 * Shows the edit task dialog
	 * 
	 * @param startmethod True to show the change method step, false for planning
	 */
	private void showEditMethod( boolean startmethod ) {
		// check if a task is selected
		final PlanTask ptask = getUI( ).getSelected( );
		if( ptask == null ) {
			ServiceProvider.this.notify( Lang.text.NoMethodSelected( ) );
			return;
		}
		
		// check if this task is one of the player's tasks
		if( !ptask.getPlayer( ).equals( getPlayer( ) ) ) {
			ServiceProvider.this.notify( Lang.text.SP_MethodEditNotMine( ptask.toString( ) ) );
			return;
		}

		// check if the player is allowed to edit this method
		if( getGameData( ).getJointPlan( ).isExecuting( ptask ) ) {
			ServiceProvider.this.notify( Lang.text.SP_MethodEditExecuting( ptask.toString( ) ) );
			return;
		}
		
		getUI( ).showEditTask( ptask, getGameData( ), startmethod, new DialogHandler<PlanChange>( ) {
			/**
			 * @see plangame.gwt.client.widgets.dialogs.DialogHandler#OK(java.lang.Object)
			 */
			@Override
			public void OK( PlanChange item ) {
				// check if anything changed
				if( item != null )
					applyChange( item, false );
			}
		} );		
	}
	
	/**
	 * Handles service provider specific events. Returns true iff the event has
	 * been processed
	 * 
	 * @param event The event that was received
	 * @return true if the event was processed successfully
	 */
	@Override
	protected boolean onEvent( Event event ) {
		// check what event has been received
		if( event instanceof AssignPortfolioEvent ) {
			// the player is assigned a new portfolio by the server, update the
			// client object to reflect the change
			setClient( ((AssignPortfolioEvent) event).getClient( ) );
						
			return true;
		} else if( event instanceof PlanEvent ) {
			if( ((PlanEvent)event).submitPlan( ) ) {
				// inform the user of the planning round
				notify( Lang.text.SP_StartingPlanning( ) );
			}
			
			// event has been processed
			return true;
		} else if( event instanceof AcceptEvent ) {
			// game manager asks the user to accept the new joint plan
			getGameData( ).setJointPlan( ((AcceptEvent)event).getJointPlan( ) );
			
			getUI( ).updateAccepting( );

			// notify the player
			notify( Lang.text.SP_AllPlansSubmitted( ) );
			return true;			
		} else if( event instanceof AcceptedEvent ) {
			// all players have responded to the accept/decline event
			if( ((AcceptedEvent)event).isAccepted( ) ) {
				notify( Lang.text.SP_JointPlanAccept( ) );
			} else {
				// restart planning
				notify( Lang.text.SP_JointPlanDecline( ) );
			}	
			return true;
		} else if( event instanceof ExecutedEvent ) {
			// the server has executed the plan for one step, update our info about
			// the game and my scores
			final GameResults res = ((ExecutedEvent)event).getResults( );

			// update results
			getGameData( ).updateResults( res );

			// might be outdated if the UI didn't update fast enough, skip UI update in that case
			/* if( res.getJointPlan( ).getGameTime( ).compareTo( getGameData( ).getCurrentTime( )) < 0 )
				return true; */
			
			updateResults( res );

			// event has been processed
			return true;
		} else if( event instanceof UpdateClientStateEvent ) {
			// the server is updating our status, reflect this change in the client
			handleStateUpdate( ((UpdateClientStateEvent)event).getState( getClient( ) ) );
			return true;
		} else if( event instanceof ReassignEvent ) {
			// the server wants to reassign the client of this view
			handleReassign( (ReassignEvent) event );
			return true;
		}
		
		// not a SP specific event, try to handle it in the GameView
		return super.onEvent( event );
	}
	
	/**
	 * Handles a state update event
	 * 
	 * @param state The new player state
	 */
	private void handleStateUpdate( ClientState state ) {
		if( state == null ) {
			warning( "State update contains no entry for client!" );
			return;
		}
		
		setState( state );
	}
	
	/**
	 * The server wants to reassign this view a new client, to restore after a
	 * unexpected disconnect. Abandon everything I am doing, set my client and
	 * acknowledge to the server
	 * 
	 * @param event The reassign event
	 */
	private void handleReassign( ReassignEvent event ) {
		// close any active dialog
		Dialog.closeActiveDialog( );
		
		// notify the client of the reassignment
		notify( Lang.text.SP_Reassigned( ) );
		
		// set the new client in this view
		setClient( event.getNewClient( ) );
		
		// acknowledge the reassignment so that the server may remove my old client
		getRPC( ).ackReassign( event.getOldClient( ).getID( ), getClient( ).getID( ), new RPCCallback<Void>( ) {
			@Override public void success( Void result ) { restoreClientState( ); }

			@Override protected String getFailureText( ) { return Lang.text.SP_AckReassignFail( ); }
		} );
	}
		
	/**
	 * @see plangame.gwt.client.view.GameView#getRPC()
	 */
	@Override
	protected SPRPCAsync getRPC( ) {
		return (SPRPCAsync)super.getRPC( );
	}
	
	/**
	 * @see plangame.gwt.client.view.GameView#getUI()
	 */
	@Override
	public ServiceProviderUI getUI( ) {
		return (ServiceProviderUI)super.getUI( );
	}
	
	/**
	 * @return My plan of the joint plan
	 */
	public Plan getPlan( ) {
		return getGameData( ).getJointPlan( ).getPlan( getClient( ).getPlayer( ) );
	}

	/**
	 * This is a player view, return the player object
	 * 
	 * @return The player that is associated with the client
	 */
	public Player getPlayer( ) {
		return getClient( ).getPlayer( );
	}

	/**
	 * Submits the current plan to the game server
	 */
	public void submitPlan( ) {
		// check if my current joint plan is valid
		final List<PlanError> valid = getGameData( ).getJointPlan( ).validate( getPlayer( ) );
		if( valid.size( ) > 0 ) {
			notify( ClientUtil.getLocalisedPlanErrors( Lang.text.SP_PlanContainsErrors( ), valid ) );
			return;
		}
		
		// valid plan, confirm submit
		final boolean unplanned = getGameData( ).getJointPlan( ).getPlanned( getPlayer( ) ).size( ) < getPlayer( ).getPortfolio( ).getTasks( ).size( );
		if( !confirm( (unplanned ? Lang.text.SP_SubmitConfirmUnplanned( ) : Lang.text.SP_SubmitConfirm( ) ) ) )
			return;
		
		// confirmed, submit
		getRPC( ).submitPlan( getClient( ).getID( ), getPlan( ), new RPCCallback<Void>( Lang.text.SP_SubmittingPlan( ) ) {
			@Override
			public void success( Void result ) {
				// change state to plan submitted
			}

			@Override protected String getFailureText( ) { return Lang.text.SP_SubmitFail( ); }
		} );
	}
	
	/**
	 * Submits response to the proposes joint plan
	 * 
	 * @param accept True if this player wants to accept the joint plan
	 */
	public void acceptPlan( boolean accept ) {
		// send the accept message
		getRPC( ).acceptPlan( getClient( ).getID( ), accept, new RPCCallback<Void>( ) {
			@Override
			public void success( Void result ) {
				// we have successfully submitted our plan
			}

			@Override protected String getFailureText( ) { return Lang.text.SP_AcceptFail( ); }
		} );
	}
	
	/**
	 * Updates the view with the execute results
	 * 
	 * @param results The game results
	 */
	private void updateResults( GameResults results ) {		
		// update UI
		getUI( ).updateResults( results );		
		
		// notify the client of any delayed methods
		for( PlanTask pt : results.getDelayed( ) )
			if( pt.getPlayer( ).equals( getPlayer( ) ) )
				notify( Lang.text.SP_MethodDelayed( pt.getMethod( ).toString( ) ) );
	
		// notify of completed methods
		for( PlanTask pt : results.getCompleted( ) )
			if( pt.getPlayer( ).equals( getPlayer( ) ) )
				notify( Lang.text.SP_MethodCompleted( pt.getMethod( ).toString( ) ) );		
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameView#applyChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public PlanTask applyChange( PlanChange change, boolean validate ) throws PlanException {
		final PlanTask ptask = super.applyChange( change, validate );
		
		// notify game server of the change
		// do not notify about delay changes as they are made by the server
		if( change.getType( ) != PlanChangeType.MethodDelaySet ) {
		
			// notify game server of change
			getRPC( ).planChange( getClient( ).getID( ), change, new RPCCallback<Void>( ) {
				@Override public void success( Void result ) { }
				
				/** @see plangame.gwt.client.util.RPCCallback#getFailureText() */
				@Override protected String getFailureText( ) { return Lang.text.SP_ChangePlanFail( ); }
			} );		
		}
		
		return ptask;
	}
	
	/**
	 * Sets the state of the client
	 * 
	 * @param newstate The new state
	 * @return The old state it was in
	 */
	private ClientState setState( ClientState newstate ) {	
		final ClientState oldstate = state;
		state = newstate;
		log( LogType.Verbose, "Changed client state to '" + newstate.toString( ) + "'" );
		
		getUI( ).updateClientState( oldstate, newstate );
		
		// if the state is finishing then show the results
		if( newstate == ClientState.Finished )
			getUI( ).showEndDialog( getPlayer( ), getGameData( ) );
	
		return oldstate;
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getClientType()
	 */
	@Override
	public ClientType getClientType( ) {
		return ClientType.ServiceProvider;
	}
	
	/**
	 * @see plangame.gwt.client.ClienViewUpdates#onClientChange(plangame.gwt.shared.clients.Client, plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void onClientChange( Client oldclient, Client newclient ) {
		// get the new client (must be SPClient here)
		final SPClient spc = (SPClient) newclient;
		if( spc == null || spc.getPlayer( ) == null ) {
			Window.setTitle( "[" + Lang.text.SP_WindowTitle( ) + "]" );
			return;
		}
		
		// get the player
		final Player p = spc.getPlayer( );
		
		// update window title
		Window.setTitle( "[" + Lang.text.SP_WindowTitle( ) + "] " + p.getDescription( ) );
		
		// update the UI
		getUI( ).updateClient( spc );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerInfoChange(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	public void onServerInfoChange( GameServerInfo serverinfo ) {
		// new server information available, update configuration
		getUI( ).updateServerInfo( serverinfo );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerStateChange(plangame.gwt.shared.state.GameServerState, plangame.gwt.shared.state.GameServerState)
	 */
	@Override
	public void onServerStateChange( GameServerState oldstate, GameServerState newstate ) {
		// server state information is updated, UI will be notified
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onClientRestore(plangame.gwt.shared.gameresponse.RestoreResponse)
	 */
	@Override
	public void onClientRestore( RestoreResponse restore ) {
		// restore my state
		setState( restore.getServerState( ).getClientStates( ).get( getClient( ) ) );
		
		// update UI
		getUI( ).updateClientRestore( );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onStartGame()
	 */
	@Override
	public void onStartGame( ) {
		getUI( ).updateStartGame( );
		
		// show the start game dialog
		if( !DebugGlobals.hideDialogs( ) )
			getUI( ).showStartDialog( getPlayer( ), getGameData( ) ); 		
	}
}
