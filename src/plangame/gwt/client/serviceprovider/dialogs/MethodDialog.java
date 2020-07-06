/**
 * @file MethodDialog.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         27 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider.dialogs;

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.game.score.PendingScore;
import plangame.game.score.TTLScore;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gamedata.GameData;
import plangame.gwt.client.resource.css.MethodDialogCSS;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.serviceprovider.ServiceProvider;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.controls.NumberSelect;
import plangame.gwt.client.widgets.controls.StepPanel;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.map.MapWidget;
import plangame.gwt.client.widgets.ttltimeline.TTLTimelineWidget;
import plangame.model.tasks.Task;
import plangame.model.tasks.TaskMethod;
import plangame.model.time.TimeDuration;
import plangame.model.time.TimePoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Joris Scharpff
 */
public class MethodDialog extends ObjectDialog<PlanChange> {
	protected interface MethodDialogUIBinder extends UiBinder<Widget, MethodDialog> { }

	/** The modes of this dialog */
	private enum DialogMode { Add, Edit }
	
	/** The current dialog mode */
	final protected DialogMode mode;
	
	/** The dialog steps */
	private enum DialogStep { Task, Method, Time, Confirm }
	
	/** Active panel */
	protected DialogStep currstep;

	/** Copy of the client game data */
	protected ClientGameData gamedata;
	
	/** The player using this dialog */
	protected Player player;
	
	/** The current planned task */
	protected PlanTask currplanned;
	
	/** Set to true to start in method edit step, false starts plan step */
	protected boolean showedit;
	
	/** If true, all network related TTL is disabled in the dialog */
	protected final boolean shownetwork;
	
	// UI elements
	// task panel
	@UiField(provided=true) protected StepPanel<Task> pnlStepTask;
	@UiField protected DataListBox<Task> lstTasks;
	@UiField protected HorizontalPanel pnlTaskInfo;
	@UiField protected Label lblTaskName;
	@UiField protected HTML lblTaskDesc;
	@UiField protected MapWidget wdMap;
	
	// method panel
	@UiField(provided=true) protected StepPanel<TaskMethod> pnlStepMethod;
	@UiField protected DataListBox<TaskMethod> lstMethods;
	@UiField protected HorizontalPanel pnlMethod;
	@UiField protected Label lblMethodName;
	@UiField protected HTML lblMethodDesc;
	@UiField protected Grid gdMethod;
	@UiField protected Label lblDuration;
	@UiField protected Label lblDelayRisk;
	@UiField protected Label lblDelayDuration;
	@UiField protected Label lblRevenue;
	@UiField protected Label lblCost;
	@UiField protected Label lblProfitExRisk;
	@UiField protected Label lblDelayCost;
	@UiField protected Label lblProfitIncRisk;
	//@UiField protected Label lblTTL;
	
	// time panel
	@UiField(provided=true) protected StepPanel<TimePoint> pnlStepTime;
	@UiField protected VerticalPanel pnlPlan;
	@UiField protected NumberSelect numWeek;
	@UiField protected TTLTimelineWidget wdPlot;
	@UiField protected Label lblTTLIndiv;
	@UiField protected Label lblTTLNetworkBestDesc;
	@UiField protected Label lblTTLNetworkBest;
	@UiField protected Label lblTTLNetworkWorstDesc;
	@UiField protected Label lblTTLNetworkWorst;
	@UiField protected Label lblTTLTotalNoDelay;
	@UiField protected Label lblTTLPaymentNoDelay;
	@UiField protected Label lblTTLIndivDelay;
	@UiField protected Label lblTTLNetworkBestDelayDesc;
	@UiField protected Label lblTTLNetworkBestDelay;
	@UiField protected Label lblTTLNetworkWorstDelayDesc;
	@UiField protected Label lblTTLNetworkWorstDelay;
	@UiField protected Label lblTTLTotalDelay;
	@UiField protected Label lblTTLPaymentDelay;
	
	// confirmation panel
	@UiField(provided=true) protected StepPanel<Boolean> pnlStepConfirm;
	@UiField protected Label lblConfirmTask;
	@UiField protected Label lblConfirmMethod;
	@UiField protected Label lblConfirmTime;
	@UiField protected Label lblConfirmRevenueRegular;
	@UiField protected Label lblConfirmRevenueDelayed;
	@UiField protected Label lblConfirmRevenueTotal;
	@UiField protected Label lblConfirmCostRegular;
	@UiField protected Label lblConfirmCostDelayed;
	@UiField protected Label lblConfirmCostTotal;
	@UiField protected Label lblConfirmPaymentRegular;
	@UiField protected Label lblConfirmPaymentDelayed;
	@UiField protected Label lblConfirmPaymentTotal;
	@UiField protected Label lblConfirmTotalCostRegular;
	@UiField protected Label lblConfirmTotalCostDelayed;
	@UiField protected Label lblConfirmTotalCostTotal;
	@UiField protected Label lblConfirmTTLIndivRegular;
	@UiField protected Label lblConfirmTTLIndivDelayed;
	@UiField protected Label lblConfirmTTLIndivTotal;
	@UiField protected Label lblConfirmNetworkBestDesc;
	@UiField protected Label lblConfirmNetworkBestRegular;
	@UiField protected Label lblConfirmNetworkBestDelayed;
	@UiField protected Label lblConfirmNetworkBestTotal;
	@UiField protected Label lblConfirmNetworkWorstDesc;
	@UiField protected Label lblConfirmNetworkWorstRegular;
	@UiField protected Label lblConfirmNetworkWorstDelayed;
	@UiField protected Label lblConfirmNetworkWorstTotal;
	@UiField protected Label lblConfirmTotalTTLRegular;
	@UiField protected Label lblConfirmTotalTTLDelayed;
	@UiField protected Label lblConfirmTotalTTLTotal;
	
	// buttons
	@UiField protected Button btnCancel;
	@UiField protected Button btnOK;
	
	/**
	 * Creates the method dialog
	 * 
	 * @param title The dialog title
	 * @param mode The dialog mode
	 * @param player The player using this dialog
	 * @param gamedata The client game data
	 * @param relative Use relative TTL display
	 * @param handler The dialog result handler
	 */
	private MethodDialog( String title, DialogMode mode, Player player, ClientGameData gamedata, boolean relative, DialogHandler<PlanChange> handler ) {
		super( title, handler );
		
		// set mode
		this.mode = mode;
		this.player = player;
		this.gamedata = gamedata.dataCopy( );
		this.shownetwork = gamedata.getJointPlan( ).anyOtherPlanned( player );
		
		// register map & plot controls for updates from this client game data
		getGameData( ).addUpdateListener( wdMap );
		wdMap.setGameData( getGameData( ) );
		wdPlot.setDisplayRelative( relative );
		getGameData( ).addUpdateListener( wdPlot );
		wdPlot.setGameData( getGameData( ) );				
		
		// add event listener to the task list
		lstTasks.addValueChangeHandler( new ValueChangeHandler<Task>( ) {
			@Override public void onValueChange( ValueChangeEvent<Task> event ) {
				setPanelValue( DialogStep.Task, event.getValue( ) );
			}
		} );
		
		// and event listener for the method list
		lstMethods.addValueChangeHandler( new ValueChangeHandler<TaskMethod>( ) {
			@Override public void onValueChange( ValueChangeEvent<TaskMethod> event ) {
				setPanelValue( DialogStep.Method, event.getValue( ) );
			}
		} );
		
		// and event listener for the time
		numWeek.addValueChangeHandler( new ValueChangeHandler<Integer>( ) {
			@Override public void onValueChange( ValueChangeEvent<Integer> event ) {
				setPanelValue( DialogStep.Time, new TimePoint( event.getValue( ) - 1 ) );
			}
		} );
		
		// and click handlers to panel buttons
		pnlStepTask.addNext( Lang.text.MethodDialog_NextStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Method ); } } );
		if( mode != DialogMode.Edit )
			pnlStepMethod.addPrevious( Lang.text.MethodDialog_PrevStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Task ); } } );
		pnlStepMethod.addNext( Lang.text.MethodDialog_NextStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Time ); } } );
		pnlStepTime.addPrevious( Lang.text.MethodDialog_PrevStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Method ); } } );
		if( useConfirm( ) ) {
			pnlStepTime.addNext( Lang.text.MethodDialog_NextStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Confirm ); } } );
			pnlStepConfirm.addPrevious( Lang.text.MethodDialog_PrevStep( ) , new ClickHandler( ) { @Override public void onClick( ClickEvent event ) { setStep( DialogStep.Time ); } } );
		} else {
			pnlStepConfirm.setVisible( false );
		}
				
		// set OK button caption
		btnOK.setText( title );
	}
	
	/**
	 * Creates the method dialog from which the player can select a task to add
	 * 
	 * @param player The player using this dialog
	 * @param gamedata The client game data
	 * @param relative Display TTL relative
	 * @param handler The dialog result handler
	 */
	public MethodDialog( Player player, ClientGameData gamedata, boolean relative, DialogHandler<PlanChange> handler ) {
		this( Lang.text.MethodDialog_AddTask( ), DialogMode.Add, player, gamedata, relative, handler );
		
		// get all unplanned tasks
		final JointPlan jplan = new JointPlan( getGameData( ).getJointPlan( ) );
		final List<Task> tasks = jplan.getUnplannedTasks( player );
		
		// mark all already planned methods in the plot
		wdPlot.setMarked( jplan.getPlanned( player ) );
		
		// remove all other methods from the player from the plan to have a fair comparison
		for( PlanTask pt : jplan.getPlanned( player ) )
			jplan.applyChange( PlanChange.remove( pt ), false );
		getGameData( ).setJointPlan( jplan );
		
		// fill task list
		lstTasks.clear( );
		lstTasks.addAll( tasks );
		lstTasks.setSelectedIndex( 0, false );
		setPanelValue( DialogStep.Task, lstTasks.getItem( 0 ) );
	}
	
	/**
	 * Creates the method dialog to edit/move a task method
	 * 
	 * @param ptask The planned task to edit
	 * @param gamedata The client game data
	 * @param showedit True to show edit method, false to show move method
	 * @param relative Show TTL relative 
	 * @param handler The dialog result handler
	 */
	public MethodDialog( PlanTask ptask, ClientGameData gamedata, boolean showedit, boolean relative, DialogHandler<PlanChange> handler ) {
		this( Lang.text.MethodDialog_EditTask( ), DialogMode.Edit, ptask.getPlayer( ), gamedata, relative, handler );
		
		// remove all other methods for fair comparison (but mark them in the plot)
		final List<PlanTask> marked = new ArrayList<PlanTask>( );
		final JointPlan jplan = new JointPlan( getGameData( ).getJointPlan( ) );
		for( PlanTask pt : jplan.getPlanned( player ) )
			if( !pt.equalsMethod( ptask ) ) {
				jplan.applyChange( PlanChange.remove( pt ), false );
				marked.add( pt );
			}
		wdPlot.setMarked( marked );
		getGameData( ).setJointPlan( jplan );
		
		// store current plan task
		this.currplanned = ptask;
		
		// set task panel value
		setPanelValue( DialogStep.Task, ptask.getTask( ) );
		setPanelValue( DialogStep.Method, currplanned.getMethod( ) );
		setPanelValue( DialogStep.Time, currplanned.getStartTime( ) );
			
		// activate the specified step
		this.showedit = showedit;
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initProvided()
	 */
	@Override
	protected void initProvided( ) {
		super.initProvided( );
		
		// initialise step panels
		pnlStepTask = new StepPanel<Task>( ) {
			@Override
			public void onValueChange( Task value ) {
				// update the task info
				lblTaskName.setText( Lang.text.MethodDialog_TaskNameTitle( value.toString( ) ) );
				lblTaskDesc.setHTML( (new SafeHtmlBuilder( ).appendEscapedLines( value.getLongDescription( ) )).toSafeHtml( ) ); 
				wdMap.setSelected( new PlanTask( value.getMethods( ).get( 0 ), new TimePoint( ), DelayStatus.Pending ), false );
			}
			
			@Override
			public String getValueText( Task value ) {
				return Lang.text.MethodDialog_TaskStepValue( value.getDescription( ) );
			}
		};		
		
		pnlStepMethod = new StepPanel<TaskMethod>( ) {
			@Override
			public void onValueChange( TaskMethod value ) {
				// set the method info and description
				lblMethodName.setText( Lang.text.MethodDialog_MethodNameTitle( value.getDescription( ) ) );
				lblMethodDesc.setHTML( (new SafeHtmlBuilder( ).appendEscapedLines( value.getLongDescription( ) )).toSafeHtml( ) );
				
				// set method duration info
				lblDuration.setText( Format.f( value.getRegularDuration( ).getWeeks( ), Format.Style.Weeks ) );
				lblDelayRisk.setText( Format.f( value.getDelayRisk( ), Format.Style.Percentage ) );
				lblDelayDuration.setText( Format.f( value.getDelayDuration( ).getWeeks( ), Format.Style.Weeks ) );
				
				// FIXME displayed cost is correct now because it is constant over time
				final double costreg = value.getCost( value.getRegularSpan( new TimePoint( ) ) );
				final double costdel = value.getCost( value.getDelaySpan( new TimePoint( ) ) );
				final double rev = value.getRevenue( );
				lblRevenue.setText( Format.f( rev, Format.Style.CurrK ) );
				lblCost.setText( Format.f( costreg, Format.Style.CurrK ) );
				lblProfitExRisk.setText( Format.f( rev - costreg, Format.Style.CurrK ) );
				lblDelayCost.setText( Format.f( costdel, Format.Style.CurrK ) );
				lblProfitIncRisk.setText( Format.f( rev - costreg - costdel, Format.Style.CurrK ) );
		}
			
			@Override
			public String getValueText( TaskMethod value ) {
				return Lang.text.MethodDialog_MethodStepValue( value.getDescription( ) );
			}
		};
		
		pnlStepTime = new StepPanel<TimePoint>( ) {
			@Override
			public void onValueChange( TimePoint value ) {
				// update the week number control
				numWeek.setValue( value.getWeek( ) + 1, false );
				
				// get all TTL info
				final TTLScore ttl = getGameData( ).getData( ).getTTL( pnlStepMethod.getValue( ).getTask( ), wdPlot.isRelative( ) );
				final TTLScore payment = getGameData( ).getData( ).getPayments( pnlStepMethod.getValue( ).getTask( ) );
				final Style style = (wdPlot.isRelative( ) ? Style.Percentage2 : Style.IntK );
				lblTTLIndiv.setText( Format.f( ttl.getIndividual( ).getRegular( ), style ) );
				lblTTLNetworkBest.setText( Format.f( ttl.getNetworkRegular( ).getBestRealised( ), style ) );
				lblTTLNetworkWorst.setText( Format.f( ttl.getNetworkRegular( ).getWorstRealised( ), style ) );
				lblTTLTotalNoDelay.setText( Format.f( ttl.getTotalRegular( ), style ) );
				lblTTLPaymentNoDelay.setText( Format.f( payment.getTotalRegular( ), Style.CurrK ) );
				lblTTLIndivDelay.setText( Format.f( ttl.getIndividual( ).getDelayed( ), style ) );
				lblTTLNetworkBestDelay.setText( Format.f( ttl.getNetworkDelayed( ).getBestRealised( ), style ) );
				lblTTLNetworkWorstDelay.setText( Format.f( ttl.getNetworkDelayed( ).getWorstRealised( ), style ) );
				lblTTLTotalDelay.setText( Format.f( ttl.getTotal( ), style ) );
				lblTTLPaymentDelay.setText( Format.f( payment.getTotal( ), Style.CurrK ) );
				
				// finally select the planned task
				wdPlot.setSelected( new PlanTask( pnlStepMethod.getValue( ), value, DelayStatus.Pending ), false );
			}
			
			@Override
			public String getValueText( TimePoint value ) {
				return Lang.text.MethodDialog_PlanStepValue( "" + (value.getWeek( ) + 1) );
			}
		};
		
		if( useConfirm( ) ) {
			pnlStepConfirm = new StepPanel<Boolean>( ) {
				/**
				 * @see plangame.gwt.client.widgets.controls.StepPanel#onValueChange(java.lang.Object)
				 */
				@Override
				public void onValueChange( Boolean value ) {
					// nothing to do
				}
				
				/**
				 * @see plangame.gwt.client.widgets.controls.StepPanel#onPanelActivate()
				 */
				@Override
				public void onPanelActivate() {
					final Task task = pnlStepTask.getValue( );
					final TaskMethod method = pnlStepMethod.getValue( );
					final TimePoint time = pnlStepTime.getValue( );
					
					lblConfirmTask.setText( task.getDescription( ) );
					lblConfirmMethod.setText( method.getDescription( ) );
					lblConfirmTime.setText( time.toString( ) );
					
					final GameData gd = getGameData( ).getData( );
					
					// cost data
					final Style c = Style.CurrK;
					final Style t = (wdPlot.isRelative( ) ? Style.Percentage2 : Style.IntK);
					
					// task revenue
					final double rev = task.getRevenue( );
					lblConfirmRevenueRegular.setText( Format.f( rev, c ) );
					lblConfirmRevenueDelayed.setText( Format.f( 0, c ) );
					lblConfirmRevenueTotal.setText( Format.f( rev, c ) );
					
					// maintenance costs
					final PendingScore cost = gd.getMaintenanceCost( task );
					lblConfirmCostRegular.setText( Format.f( -cost.getRegular( ), c ) );
					lblConfirmCostDelayed.setText( Format.f( -cost.getDelayed( ), c ) );
					lblConfirmCostTotal.setText( Format.f( -cost.getTotal( ), c ) );
					
					// payments
					final TTLScore payments = gd.getPayments( task );
					lblConfirmPaymentRegular.setText( Format.f( -payments.getTotalRegular( ), c ) );
					lblConfirmPaymentDelayed.setText( Format.f( -payments.getTotalDelay( ), c ) );
					lblConfirmPaymentTotal.setText( Format.f( -payments.getTotal( ), c ) );
					
					// totals
					lblConfirmTotalCostRegular.setText( Format.f( rev - cost.getRegular( ) - payments.getTotalRegular( ), c ) );
					lblConfirmTotalCostDelayed.setText( Format.f( -cost.getDelayed( ) - payments.getTotalDelay( ), c ) );
					lblConfirmTotalCostTotal.setText( Format.f( rev - cost.getTotal( ) - payments.getTotal( ), c ) );
					
					// TTL data
					final TTLScore ttl = gd.getTTL( task, wdPlot.isRelative( ) );
					
					// individual
					lblConfirmTTLIndivRegular.setText( Format.f( ttl.getIndividual( ).getRegular( ), t ) );
					lblConfirmTTLIndivDelayed.setText( Format.f( ttl.getIndividual( ).getDelayed( ), t ) );
					lblConfirmTTLIndivTotal.setText( Format.f( ttl.getIndividual( ).getTotal( ), t ) );
					
					// network best-case
					lblConfirmNetworkBestRegular.setText( Format.f( ttl.getNetworkRegular( ).getBestRealised( ), t ) );
					lblConfirmNetworkBestDelayed.setText( Format.f( ttl.getNetworkDelayed( ).getBestRealised( ), t ) );
					lblConfirmNetworkBestTotal.setText( Format.f( ttl.getNetworkRegular( ).getBestRealised( ) + ttl.getNetworkDelayed( ).getBestRealised( ), t ) );
	
					// network worst-case
					lblConfirmNetworkWorstRegular.setText( Format.f( ttl.getNetworkRegular( ).getWorstRealised( ), t ) );
					lblConfirmNetworkWorstDelayed.setText( Format.f( ttl.getNetworkDelayed( ).getWorstRealised( ), t ) );
					lblConfirmNetworkWorstTotal.setText( Format.f( ttl.getNetworkRegular( ).getWorstRealised( ) + ttl.getNetworkDelayed( ).getWorstRealised( ), t ) );
					
					// ttl totals
					lblConfirmTotalTTLRegular.setText( Format.f( ttl.getTotalRegular( ), t ) );
					lblConfirmTotalTTLDelayed.setText( Format.f( ttl.getTotalDelay( ), t ) );
					lblConfirmTotalTTLTotal.setText( Format.f( ttl.getTotal( ), t ) );
				}
				
				/**
				 * @see plangame.gwt.client.widgets.controls.StepPanel#getValueText(java.lang.Object)
				 */
				@Override
				public String getValueText( Boolean value ) {
					return "";
				}
			};
		} else {
			// create place-holder, otherwise GWT crashes (initprovided=null)
			pnlStepConfirm = new StepPanel<Boolean>( ) {
				@Override public void onValueChange( Boolean value ) { }
				
				@Override public String getValueText( Boolean value ) { return null; }
			};
		}
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((MethodDialogUIBinder)GWT.create( MethodDialogUIBinder.class )).createAndBindUi( this );
	}	
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
				
		// move OK button to the far right
		btnOK.getElement( ).getParentElement( ).getStyle( ).setProperty( "textAlign", "right" );
		
		// set escape and enter buttons
		setCancel( btnCancel );
		// enter key in textbox closes the window if enabled
		// setDefault( btnOK );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#onShow()
	 */
	@Override
	protected void onShow( ) {
		super.onShow( );		
			
		// force the map size
		// FIXME make CSS configurable
		wdMap.setSize( "200px", "200px" );
		wdPlot.setPixelSize( 440, 235 );
		
		// FIXME update X axis in another way
		wdPlot.setXAxis( getGameData( ).getGamePeriod( ) );
		wdPlot.setDrawNetwork( shownetwork );
		
		// setup display of network TTL
		if( !shownetwork ) {
			lblTTLNetworkBestDesc.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkBest.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkBestDelayDesc.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkBestDelay.addStyleName( getCSS( ).disabled( ) );			
			lblTTLNetworkWorstDesc.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkWorst.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkWorstDelayDesc.addStyleName( getCSS( ).disabled( ) );
			lblTTLNetworkWorstDelay.addStyleName( getCSS( ).disabled( ) );

			if( useConfirm( ) ) {
				lblConfirmNetworkBestDesc.addStyleName( getCSS( ).disabled( ) );
				lblConfirmNetworkBestRegular.addStyleName( getCSS( ).disabled( ) );			
				lblConfirmNetworkBestDelayed.addStyleName( getCSS( ).disabled( ) );			
				lblConfirmNetworkBestTotal.addStyleName( getCSS( ).disabled( ) );
				lblConfirmNetworkWorstDesc.addStyleName( getCSS( ).disabled( ) );
				lblConfirmNetworkWorstRegular.addStyleName( getCSS( ).disabled( ) );			
				lblConfirmNetworkWorstDelayed.addStyleName( getCSS( ).disabled( ) );			
				lblConfirmNetworkWorstTotal.addStyleName( getCSS( ).disabled( ) );
			}
		}		
				
		// select the current info
		if( mode == DialogMode.Add ) {
			// select the task
			setStep( DialogStep.Task );
		} else {						
			setStep( showedit ? DialogStep.Method : DialogStep.Time );
			// reset value to display graph correctly
			setPanelValue( DialogStep.Time, pnlStepTime.getValue( ) );
		}		
	}
	
	/**
	 * Sets the currently active step, checks whether this is allowed (i.e. user
	 * made a decision in the step)
	 * 
	 * @param newstep The new step
	 */
	private void setStep( DialogStep newstep ) {
		final DialogStep prevstep = currstep;
		if( currstep == newstep ) return;
		
		// check if allowed
		if( (newstep == DialogStep.Method || newstep == DialogStep.Time) && pnlStepTask.getValue( ) == null ) {
			ClientView.getInstance( ).notify( Lang.text.SelectTask( ) );
			return;
		}
		if( newstep == DialogStep.Time && pnlStepMethod.getValue( ) == null ) {
			ClientView.getInstance( ).notify( Lang.text.SelectMethod( ) );
			return;
		}

		// set step
		currstep = newstep;
		
		// make sure a method is selected
		if( prevstep == DialogStep.Task ) {
			setPanelValue( DialogStep.Method, lstMethods.getItem( 0 ) );
		} else if( prevstep == DialogStep.Method && newstep == DialogStep.Time ) {
			if( pnlStepTime.getValue( ) == null )
				setPanelValue( DialogStep.Time, new TimePoint( ) );
		}
		
		// update the interface depending on the current step
		pnlStepTask.setActive( currstep == DialogStep.Task );
		pnlStepMethod.setActive( currstep == DialogStep.Method );
		pnlStepTime.setActive( currstep == DialogStep.Time );
		if( useConfirm( ) )
			pnlStepConfirm.setActive( currstep == DialogStep.Confirm  );
		
		// enable add button only in last step
		btnOK.setEnabled( (mode == DialogMode.Add && currstep == (useConfirm( ) ? DialogStep.Confirm : DialogStep.Time)) || (mode == DialogMode.Edit) );
	}
	
	/**
	 * Sets the step value
	 * 
	 * @param step The step to set value of
	 * @param value The value to set
	 */
	private void setPanelValue( DialogStep step, Object value ) {
		// check what panel to set
		switch( step ) {
			case Task:
				final Task task = (Task) value;
				pnlStepTask.setValue( task );
				
				// fill methods
				lstMethods.clear( );
				lstMethods.addAll( task.getMethods( ) );				
				break;
				
			case Method: {
				final TaskMethod method = (TaskMethod) value;
				pnlStepMethod.setValue( method );
				
				// select the method from the combo
				lstMethods.setSelectedItem( method, false );
				
				// also update time point (change to latest possible if a change of
				// method results in an invalid time point)
				if( pnlStepTime.getValue( ) != null ) {
					TimePoint curr = pnlStepTime.getValue( );
					if( curr.add( method.getTotalDuration( ) ).compareTo( getGameData( ).getEndTime( ) ) > 0 )
						curr = getGameData( ).getEndTime( ).subtract( method.getTotalDuration( ) ).add( new TimeDuration( 1 ) );
					
					setPanelValue( DialogStep.Time, curr );
				}
				break;
			}
				
			case Time: {
				final TaskMethod method = pnlStepMethod.getValue( );
				final TimePoint time = (TimePoint) value;
				
				// update the joint plan with the new method/time combination
				final PlanTask prev = getGameData( ).getJointPlan( ).getPlanned( method.getTask( ) );
				final PlanChange c;
				if( prev == null ) {
					c = PlanChange.add( method, time );
				} else if( !prev.getMethod( ).equals( method ) ) {
					c = PlanChange.change( prev, method, time );
				} else {
					c = PlanChange.move( prev, time ); 
				}
				getGameData( ).changeJointPlan( c, false );
				
				// update week select range
				numWeek.setMax( getGameData( ).getGamePeriod( ).getWeeks( ) - method.getTotalDuration( ).getWeeks( ) + 1 );
				numWeek.setMin( getGameData( ).getCurrentTime( ).getWeek( ) + 1 );				

				// set the value in the panel
				pnlStepTime.setValue( time );
				break;
			}
			
			case Confirm: break;
		}
	}
		
	/**
	 * Button handler for the cancel button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnCancel")
	protected void onCancel( ClickEvent e ) {
		Cancel( );
	}
	
	/**
	 * Button handler for the OK button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnOK")
	protected void onOK( ClickEvent e ) {		
		// get the new method
		final TaskMethod method = lstMethods.getSelectedItem( );
		if( method == null ) {
			ClientView.getInstance( ).notify( Lang.text.SelectMethod( ) );
			return;
		}
		
		// get the new time point from the number select
		final TimePoint time = pnlStepTime.getValue( );
		
		// check what change to return
		final PlanChange change;
		switch( mode ) {
			default: // shuts up the compiler
			case Add:
				change = PlanChange.add( method, time );
				break;
				
			case Edit:
				final PlanTask ptask = getGameData( ).getJointPlan( ).getPlanned( method.getTask( ) );
				
				// check the type of edit
				if( !method.equals( currplanned.getMethod( ) ) )
					change = PlanChange.change( ptask, method, time );
				else 
					change = PlanChange.move( ptask, time );
		}
		
		OK( change );
	}
	
	/**
	 * @return The game data object
	 */
	private ClientGameData getGameData( ) {
		return gamedata;
	}
	
	/**
	 * @return The method dialog CSS
	 */
	private MethodDialogCSS getCSS( ) {
		return ClientView.getInstance( ).getResources( ).methoddialogcss( );
	}
	
	/**
	 * @return True if the confirm step should be enabled
	 */
	private boolean useConfirm( ) { 
		return ServiceProvider.getInstance( ).getGameConfig( ).confirmStep( );
	}
}
