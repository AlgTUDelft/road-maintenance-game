/**
 * @file PlanViewWIdget.java
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
 * @date         17 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.plan;

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.GameInfoUpdateListener;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.resource.css.PlanWidgetCSS;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.shared.state.GameInfo;
import plangame.model.tasks.TaskMethod;
import plangame.model.time.TimeDuration;
import plangame.model.time.TimePoint;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PlanWidget extends BasicWidget implements GameInfoUpdateListener, JointPlanUpdateListener {	
	/** The HTML components */
	@UiField protected FocusPanel fcPanel;
	@UiField protected AbsolutePanel pnlPlanning;
	
	/** The drawing variables, stored for future use */
	protected DrawingVars drawvars;	
	
	/** The current week marker */
	protected Panel weekmarker;
	
	/** The player labels */
	protected List<Label> playerlabels;
	
	/** The player using this plan widget, null for GM */
	protected Player player;
	
	/** True to show method names of other players (false by default) */
	protected boolean showothernames;
	
	/** True if the plan widget is currently editable */
	protected boolean editable;

	/** Order of players in the control */
	protected List<Player> players;
	
	/**
	 * Creates a new plan view widget
	 */
	public PlanWidget( ) {
		super( );
	
		// create focus and absolute panels to contain everything
		pnlPlanning = new AbsolutePanel( );
		pnlPlanning.setSize( "100%", "100%" );
		fcPanel = new FocusPanel( pnlPlanning );
		fcPanel.setSize( "100%", "100%" );

		// click handler for the focus panel
		fcPanel.addMouseUpHandler( new MouseUpHandler( ) {
			@Override public void onMouseUp( MouseUpEvent event ) {
				// deselect current method
				deselect( );
			}
		} );
		
		// initialise the widget
		initWidget( fcPanel );
		ClientUtil.disableTextSelect( getElement( ) );
		setShowOtherMethodNames( false );
		
		// disable editing
		setEditable( false );
	}
		
	/**
	 * Sets the player using this widget
	 * 
	 * @param player The player
	 */
	public void setPlayer( Player player ) {
		this.player = player;
		
		// modify the players list such that the now selected player is on top
		if( !gameDataReady( ) ) return;
		setPlayerList( getGameData( ).getPlayers( ) );
	}
	
	/** @return The player using this widget */
	public Player getPlayer( ) { return player; }
	
	/**
	 * Sets the players that are displayed in this control, the list will be
	 * modified to have the player assigned to this control as the first element
	 * 
	 * @param players The new player list
	 */
	private void setPlayerList( List<Player> players ) {
		this.players = players;
		
		// make suer the assigned player is first
		if( getPlayer( ) != null ) {
			players.remove( getPlayer( ) );
			players.add( 0, getPlayer( ) );
		}
		
		// update control if possible
		if( drawvars == null ) return;
		
		// update player labels
		for( int i = 0; i < players.size( ); i++ )
			playerlabels.get( i ).setText( players.get( i ).getDescription( ) );
		
		// update the displayed task methods
		for( PlanTaskWidget tw : getTaskWidgets( ) )
			tw.updatePlayerIndex( );				
	}
	
	/** @return The player list */
	private List<Player> getPlayerList( ) { return players; }
	
	/**
	 * Sets the mode to executing to slightly alter the planning
	 * 
	 * @param executing True to enable execution mode
	 */
	public void setExecuting( boolean executing ) {
		if( weekmarker == null ) return;
		
		weekmarker.setStyleName( getCSS( ).weekmarkerexecuting( ), executing );
	}
	
	/**
	 * Updates the week marker, called after execution
	 */
	public void updateWeekMarker( ) {
		pnlPlanning.setWidgetPosition( weekmarker, drawvars.getGridX( getGameData( ).getCurrentTime( ) ), drawvars.top );		
	}
	
	/**
	 * Enable or disable display of method names of other players
	 * 
	 * @param show True to show method names of others
	 */
	public void setShowOtherMethodNames( boolean show ) {
		this.showothernames = show;
		
		// update task widgets to be sure
		if( gameDataReady( ) )
			updateTaskWidgets( );
	}
	
	/**
	 * @return True if method names of other players are shown
	 */
	public boolean showOtherMethodNames( ) { return showothernames; }

	/**
	 * Enables/disables editing of this widget
	 * 
	 * @param editable True to enable editing
	 */
	public void setEditable( boolean editable ) {
		this.editable = editable;
	}
	
	/** @return True if the widget is editable */
	public boolean isEditable( ) { return editable; }

	/**
	 * Gets the pidx of the method
	 * 
	 * @param method The task method
	 * @return The pidx of the row that corresponds to the plan of the player
	 * that has the method in its portfolio 
	 */
	public int getPIdx( TaskMethod method ) {
		for( int i = 0; i < getPlayerList( ).size( ); i++ )
			if( getPlayerList( ).get( i ).equals( method.getPlayer( ) ) )
				return i;
		
		return -1;
	}
	
	/**
	 * Finds the task widget corresponding to the planned task
	 * 
	 * @param ptask The planned task
	 * @return The task widget or null if it is not planned
	 */
	private PlanTaskWidget getTaskWidget( PlanTask ptask ) {
		if( ptask == null ) return null;
		
		for( int i = 0; i < pnlPlanning.getWidgetCount( ); i++ ) {
			Widget w = pnlPlanning.getWidget( i );
			
			if( w instanceof PlanTaskWidget && ((PlanTaskWidget)w).getPlannedTask( ).getMethod( ).equals( ptask.getMethod( ) ) )
				return (PlanTaskWidget)w;
		}
		
		return null;
	}
	
	/**
	 * Retrieves the plan widget CSS
	 * 
	 * @return The CSS rules
	 */
	protected PlanWidgetCSS getCSS( ) {
		return ClientView.getInstance( ).getResources( ).plancss( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.GameInfoUpdateListener#onGameInfoSet(plangame.gwt.shared.state.GameInfo)
	 */
	@Override
	public void onGameInfoSet( GameInfo gameinfo ) {
		// create players list
		setPlayerList( gameinfo.getPlayers( ) );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		drawvars = null;
		repaint( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		switch( change.getType( ) ) {
			case MethodAdded:
				// create new widget
				updateTaskWidget( change.getResult( ), true );
				break;
				
			case MethodChanged:
				// update the widget by replacing the method
				updateTaskWidget( change.getPrevious( ), change.getResult( ) );
				break;
				
			case MethodMoved:
				// update the widget
				updateTaskWidget( change.getResult( ), false );
				break;

			case MethodRemoved:
				removeTaskWidget( change.getPrevious( ) );
				break;
				
			case MethodDelaySet:
				updateTaskWidget( change.getResult( ), false );
				break;
		}
		
		drawvars.recomputeRows( getGameData( ).getJointPlan( ) );
		updateRows( );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection,	PlanTask newselection ) {
		super.onSetSelection( oldselection, newselection );

		// notify task widgets of the new selections
		final PlanTaskWidget oldsel = getTaskWidget( oldselection );
		if( oldsel != null ) oldsel.setSelected( newselection, false );
		final PlanTaskWidget newsel = getTaskWidget( newselection );
		if( newsel != null ) newsel.setSelected( newselection, false );
	}
			
	/**
	 * Redraws the planning
	 */
	protected void repaint( ) {		
		// check if the data is ready for draw
		if( !gameDataReady( ) ) return;
		paintStart( );

		// check if there is at least one player connected, otherwise drawing is
		// useless
		final JointPlan jplan = getGameData( ).getJointPlan( );
		if( jplan.getPlayers( ).size( ) == 0 ) return;
		
		// check if UI is already drawn 
		if( drawvars == null ) {		
			pnlPlanning.clear( );
			
			// setup drawing parameters and store them for future reference
			try {
				drawvars = new DrawingVars( this );
			} catch( IllegalStateException ise ) {
				// the planning widget is not active, retry later
				paintEnd( "Not able to complete paint: " + ise.getMessage( ) );
				return;
			}
			
			// draw week number label
			final HTML weeklbl = new HTML( Lang.text.Week( ) + "  " );
			weeklbl.setStyleName( getCSS( ).weeklegend( ) );
			weeklbl.setSize( drawvars.left + "px", drawvars.bottom + "px" );
			pnlPlanning.add( weeklbl );
			pnlPlanning.setWidgetPosition( weeklbl, 0, drawvars.gridh + drawvars.top );
	
			// draw the planning grid
			final Grid grid = new Grid( drawvars.N, drawvars.weeks );
			grid.setStyleName( getCSS( ).plangrid( ) );
			grid.setSize( drawvars.gridw + "px", drawvars.gridh + "px" );
			pnlPlanning.add( grid );
			pnlPlanning.setWidgetPosition( grid, drawvars.left, drawvars.top );
			
			// draw the week label every X weeks
			final int numweeks = 2;
			final Grid weeklbls = new Grid( 1, drawvars.weeks / numweeks + (drawvars.weeks % numweeks > 0 ? 1: 0) );
			weeklbls.setStyleName( getCSS( ).weeklabel( ) );
			weeklbls.setSize( drawvars.gridw + "px", drawvars.bottom + "pw" );
			for( int week = 0; week < drawvars.weeks / numweeks; week ++ ) {
				weeklbls.setText( 0, week, "" + (week + 1) * numweeks );
				// small correction for the grid border
				weeklbls.getCellFormatter( ).setWidth( 0, week, (drawvars.xperw - 1) + "px" );
			}
			pnlPlanning.add( weeklbls );
			pnlPlanning.setWidgetPosition( weeklbls, drawvars.left, drawvars.top + drawvars.gridh );
			
			// draw the current week marker
			weekmarker = new HTMLPanel( "" );
			weekmarker.setStyleName( getCSS( ).weekmarker( ) );
			weekmarker.setSize( drawvars.xperw + "px", (drawvars.gridh + drawvars.bottom) + "px" );
			pnlPlanning.add( weekmarker );
			updateWeekMarker( );
			
			// draw the player labels
			playerlabels = new ArrayList<Label>( players.size( ) );
			for( Player p : getPlayerList( ) ) {
				// create the player labels
				final HTML lbl = new HTML( p.toString( ) );
				lbl.setStyleName( getCSS( ).playerlabel( ) );
				lbl.setWidth( (drawvars.left - 10) + "px" );
				lbl.setHeight( drawvars.yperp + "px" );
				pnlPlanning.add( lbl );
				pnlPlanning.setWidgetPosition( lbl, 0, drawvars.top + drawvars.yperp * players.indexOf( p ) );	
				
				playerlabels.add( lbl );
			}
		}

		// redraw all task widgets
		for( PlanTaskWidget tw : getTaskWidgets( ) )
			pnlPlanning.remove( tw );
		for( PlanTask pt : jplan.getPlanned( ) )
			updateTaskWidget( pt, true );	

		
		updateRows( );
		paintEnd( );
	}
	
	/**
	 * Updates the rows in which task widgets are displayed
	 */
	protected void updateRows( ) {
		// clear all row numbers and check for overlap
		for( PlanTaskWidget tw : getTaskWidgets( ) )
			tw.clearRow( );
		setTaskRows( );		
	}
	
	/**
	 * Creates or updates the task widget for the specified method and adds it to
	 * the plan panel
	 * 
	 * @param ptask The planned task to add/update
	 */
	private PlanTaskWidget createTaskWidget( PlanTask ptask ) {
		// add a new task widget
		final PlanTaskWidget tw = new PlanTaskWidget( this );
		pnlPlanning.add( tw );

		// setup the widget
		tw.setPlannedTask( ptask );
		tw.setDragHandler( new PlanTaskWidgetDragHandler( tw, getGameData( ).getGamePeriod( ).getWeeks( ) ) );
		return tw;
	}
	
	/**
	 * Updates all task widgets
	 */
	private void updateTaskWidgets( ) { 
		for( PlanTaskWidget tw : getTaskWidgets( ) )
			updateTaskWidget( tw.getPlannedTask( ), false );
	}
	
	/**
	 * Updates the task widget info
	 * 
	 * @param ptask The planned task
	 * @param create True to create the widget
	 * @return The updated task widget
	 */
	public PlanTaskWidget updateTaskWidget( PlanTask ptask, boolean create ) {
		// get the task widget for the method
		PlanTaskWidget tw = getTaskWidget( ptask );
		if( tw == null ) {
			if( !create ) throw new RuntimeException( "No task widget for method '" + ptask.getMethod( ).toString( ) + "'" );
			tw = createTaskWidget( ptask );
		}

		// set the planned task
		tw.setPlannedTask( ptask );
		
		// allow selection only on my widgets
		//tw.setSelectable( ptask.getPlayer( ).equals( getPlayer( ) ) );
		
		// setup the task widget
		tw.setWeek( ptask.getStartTime( ) );
		
		// check if I am selected
		if( getSelected( ) != null && ptask.equalsMethod( getSelected( ) ) )
			tw.setSelected( ptask, false );
		
		return tw;
	}
	
	/**
	 * Updates the task widget by replacing the method
	 * 
	 * @param prev The current planned task in the task widget
	 * @param newtask The planned task to replace it with
	 */
	private void updateTaskWidget( PlanTask prev, PlanTask newtask ) {
		// set the method of the widget
		final PlanTaskWidget tw = getTaskWidget( prev );
		if( tw == null ) throw new RuntimeException( "No task widget for method '" + prev.toString( ) + "'" );
		
		// replace the method
		tw.setPlannedTask( newtask );
		updateTaskWidget( newtask, false );
	}
	
	/**
	 * Removes the task widget for the planned task
	 * 
	 * @param ptask The planned task
	 */
	public void removeTaskWidget( PlanTask ptask ) {
		final PlanTaskWidget tw = getTaskWidget( ptask );
		if( tw == null ) throw new RuntimeException( "No method '" + ptask.getMethod( ).toString( ) + "' in the plan" );
		
		// remove the task widget and clear reference for GBC
		pnlPlanning.remove( tw );
		tw.planwidget = null;
	}

	
	/**
	 * Checks the plans for possible overlaps and adjusts the plan display so
	 * that each task is displayed in the right row
	 */
	private void setTaskRows( ) {
		final JointPlan jplan = getGameData( ).getJointPlan( );
				
		// go through the plans and assign first available row to each widget
		for( Player p : getPlayerList( ) ) {			
			for( TimePoint week : jplan.getPeriod( ).toWeeks( ) ) {
				final List<PlanTask> planned = jplan.getPossiblyPlannedAt( p, week );

				for( PlanTask pt : planned ) {
					final PlanTaskWidget tw = getTaskWidget( pt );
					
					// only set index if this has not been done yet
					if( tw.getRow( ) == -1 ) {
						tw.setRow( getFirstAvailableRow( planned ) );
					}					
				}
			}
		}
	}
	
	/**
	 * Retrieves the first available row number for the planned tasks
	 * 
	 * @param planned The planned tasks
	 * @return The first available row number
	 */
	private int getFirstAvailableRow( List<PlanTask> planned ) {
		for( int i = 0; i < planned.size( ); i++ ) {
			boolean found = false;
			for( PlanTask pt : planned ) {
				final PlanTaskWidget tw = getTaskWidget( pt );
				if( tw.getRow( ) == i ) found = true;
			}
			
			if( !found ) return i;
		}
		
		return -1;
	}

	/**
	 * Retrieves all task widgets that are currently on display
	 * 
	 * @return List of all displayed task widgets
	 */
	private List<PlanTaskWidget> getTaskWidgets( ) {
		final List<PlanTaskWidget> tasks = new ArrayList<PlanTaskWidget>( );
		for( int i = 0; i < pnlPlanning.getWidgetCount( ); i++ ) {
			final Widget w = pnlPlanning.getWidget( i );
			if( w instanceof PlanTaskWidget ) tasks.add( (PlanTaskWidget)w );
		}
		return tasks;
	}
	
	/**
	 * Called by the plan widget to check if the task associated with the
	 * widget may be dragged by the user
	 * 
	 * @param ptask The planned task
	 * @return True if the task is draggable
	 */
	protected boolean canDrag( PlanTask ptask ) {
		// drag only if editable
		if( !isEditable( ) ) return false;
		
		// can only drag non-executed methods
		if( getGameData( ).getJointPlan( ).isExecuting( ptask ) )
			return false;
		
		// can only drag my methods
		if( !ptask.getPlayer( ).equals( getPlayer() ) )
			return false;
		
		return true;
	}

	/**
	 * Drawing variables
	 */
	static class DrawingVars {
		/** The planwidget for which this drawing vars is used */
		protected PlanWidget planwidget;
		
		/** The last dimensions we have computed for */
		protected int width;
		protected int height;
		
		// joint plan vars
		protected int longestname;
		protected int weeks;
		protected int N;
		
		// fixed margins
		protected int bottom;
		protected int top;
		protected int left;
		protected int right;

		// pixels per week / player in the grid
		protected int xperw;
		protected int yperp;
		
		// the number of rows displayed per player
		protected int[] rows;
		
		// plan grid dimensions
		protected int gridw;
		protected int gridh;

		/**
		 * Creates a new drawingvars object
		 * 
		 * @throws IllegalStateException if the drawvars are invalid
		 */
		protected DrawingVars( PlanWidget pw ) throws IllegalStateException {
			this.planwidget = pw;

			// store dimensions
			this.width = planwidget.getOffsetWidth( );
			this.height = planwidget.getOffsetHeight( );

			// sizes not set, the widget is not yet active
			if( width == 0 || height == 0 )
				throw new IllegalStateException( "Invalid widget size: [" + width + " x " + height + "]" );
			
			// recompute vars
			recomputePlanVars( planwidget.getGameData( ).getJointPlan( ) );
			recompute( );
			recomputeRows( planwidget.getGameData( ).getJointPlan( ) );
		}
		
		/**
		 * Compute drawing variables such as margins etc, the joint plan vars
		 * should be updated before calling this function to reflect the current
		 * joint plan correctly
		 */
		protected void recompute( ) {
			// compute minimal margins
			final int minleft = longestname * 8 + 20; // minimal left for player names
			final int minbottom = 30; // minimal bottom for week labels

			// compute the pixels per week / player, rounded down to the nearest integer
			xperw = (width - minleft - right) / weeks;
			yperp = (height - top - minbottom) / N;
			
			// compute the grid size to match the pixels per week / player, this
			// fixes most possible rounding errors
			gridw = xperw * weeks;
			gridh = yperp * N;
			
			// set margins
			right = 10;
			top = 10;
			left = width - gridw - right;
			bottom = height - gridh - top;
		}
		
		/**
		 * Recompute all vars that are affected by the joint plan
		 * 
		 * @param jplan The joint plan
		 */
		protected void recomputePlanVars( JointPlan jplan ) {
			// compute length of longest player name, if unknown
			for( Player p : jplan.getPlayers( ) )
				if( p.toString( ).length( ) > longestname )
					longestname = p.toString( ).length( );
			
			// compute joint plan constants
			weeks = jplan.getPeriod( ).getWeeks( );
			N = jplan.getPlayers( ).size( );
		}
		
		/**
		 * Computes the displayed rows for each player
		 * 
		 * @param jplan The joint plan
		 */
		protected void recomputeRows( JointPlan jplan ) {
			rows = new int[ N ];
			
			// compute the number of rows for each player
			for( int p = 0; p < planwidget.getPlayerList( ).size( ); p++ ) {
				rows[ p ] = 1;
				final Player pl = planwidget.getPlayerList( ).get( p );
				
				// get overlaps per week
				for( TimePoint week : jplan.getPeriod( ).toWeeks( ) ) {
					final List<PlanTask> planned = jplan.getPossiblyPlannedAt( pl, week );
					final int num = planned.size( );
					if( num > rows[ p ] ) rows[ p ] = num;
				}
			}

		}
				
		/**
		 * Computes the X coordinate that corresponds to the specified week
		 * 
		 * @param week The week number
		 * @return The leftmost X coordinate in the week column
		 */
		protected int getGridX( TimePoint week ) {
			return left + getWeekWidth( new TimeDuration( week.getWeek( ) ) );
		}
		
		/**
		 * Computes the week that corresponds to the specified X coordinate in the
		 * plan widget, bounded between 0 and weeks
		 * 
		 * @param X The x coordinate
		 * @return The week number
		 */
		protected int getGridWeek( int X ) {
			final int week = (X - left) / xperw;
			
			if( week < 0 ) return 0;
			if( week > weeks ) return weeks;
			
			return week;
		}
		
		/**
		 * Computes the grid width that corresponds to the specified number of
		 * weeks
		 * 
		 * @param weeks The number of weeks
		 * @return The width of the period in the grid coordinates
		 */
		protected int getWeekWidth( TimeDuration weeks ) {
			return xperw * weeks.getWeeks( );
		}
		
		/**
		 * Computes the Y coordinate that corresponds to the specified player
		 * 
		 * @param player The player
		 * @return The top coordinate of the player row
		 */
		protected int getGridY( Player player ) {
			return getGridY( player, 0 );
		}
		
		/**
		 * Computes the Y coordinate that corresponds to the specified row of the
		 * player's plan
		 * 
		 * @param player The player
		 * @param row The row index
		 * @return The top coordinate of the row within the player row 
		 */
		protected int getGridY( Player player, int row ) {
			return (int) (top + yperp * getPlayerIdx( player ) + getRowHeight( player ) * row);
		}
		
		/**
		 * Computes the height that corresponds to one row within a player row
		 * 
		 * @param player The player
		 * @return The row height
		 */
		protected double getRowHeight( Player player ) {
			return yperp / rows[ getPlayerIdx( player )  ];
		}
		
		/**
		 * Gets the index of the player in the displayed plans
		 * 
		 * @param player The player
		 * @return It's index
		 */
		protected int getPlayerIdx( Player player ) {
			return planwidget.getPlayerList( ).indexOf( player );
		}
		
		/**
		 * DEBUG prints the vars
		 */
		public void print( ) {
			System.out.println( "Drawing variables" );
			System.out.println( "  Jointplan: longest = " + longestname + ", N = " + N + ", weeks = " + weeks );
			System.out.println( "  Grid size: width = " + gridw + ", height = " + height );
			System.out.println( "  Margins: [l " + left + ", r " + right + ", t " + top + ", b " + bottom + "]" );
			System.out.println( "  Resolution: Xres = " + xperw + ", Yres = " + yperp );
		}
	}
}