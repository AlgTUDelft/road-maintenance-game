/**
 * @file PlotWidget.java
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
 * @date         15 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.ttltimeline;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.resource.css.TTLTimelineWidgetCSS;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.serviceprovider.ServiceProvider;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.controls.HyperLabel;
import plangame.gwt.client.widgets.listeners.TTLWidget;
import plangame.model.functions.Function;
import plangame.model.time.TimeDuration;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget for KPI plots
 *
 * @author Joris Scharpff
 */
// FIXME separate plot functionality and TTL stuff into two widgets
public class TTLTimelineWidget extends BasicWidget implements TTLWidget, JointPlanUpdateListener {
	
	/** HTML components */
	protected HorizontalPanel pnlMain;
	protected AbsolutePanel pnlPlot;
	protected VerticalPanel pnlLegend;
	
	/** The Canvas widget */
	protected Canvas canvas;
	
	/** List of plot functions */
	protected List<Plot> plots;
	
	/** The player for whom we are plotting */
	protected Player player;
	
	/** The list of planned tasks to mark in the plot */
	protected List<PlanTask> marked;
	
	/** Plot relative / absolute figures */
	protected boolean relative;
	
	/** X-axis labels */
	protected List<Label> xlabels;
	/** X-axis caption label */
	protected Label xcaption;
	
	/** Y-axis labels */
	protected List<Label> ylabels;
	/** Y-axis caption label */
	protected Label ycaption;
		
	/** Margins for axes/legend */
	protected final double left = 170.0; 
	protected final double right = 10.0; 
	protected final double top = 10.0; 
	protected final double bottom = 140.0;
	
	protected final double XLABEL_WIDTH = 32;
	protected final double XLABEL_MIN = 30;
	protected final double XLABEL_YOFFSET = -35;
	protected final double XCAPTION_YOFFSET = -25;
	protected final double YLABEL_WIDTH = 32;
	protected final double YLABEL_MIN = 30;
	protected final double YLABEL_XOFFSET = 20;
	protected final double YCAPTION_XOFFSET = 20;
	protected final double PLOTAREA_FACT = 0.75;

	/** Plot options */
	private static final double AXES_WIDTH = 3;
	private static final double PLOT_WIDTH = 5;
	private static final double TASK_WIDTH = 2;

	/** Plot options */
	private PlotOptions options;
	
	/** Cached x values */
	protected TimePoint[] values_x;
	
	/** Cached y values per plot */
	protected double[][] values_y;
	
	/** The actual plot area height */
	protected double width;
	
	/** The actual plot area height */
	protected double height;
	
	/** Cached plots (quick update if only selection changes) */
	protected ImageData plotcache;
	
	/**
	 * Creates a new PlotWidget
	 */
	public TTLTimelineWidget( ) {
		super( );
		
		// create new options object and clear cache
		options = new PlotOptions( );
		plotcache = null;
		setDisplayRelative( false );
		setMarked( new ArrayList<PlanTask>( ) );

		// create panels
		pnlMain = new HorizontalPanel( );
		pnlPlot = new AbsolutePanel( );
		pnlLegend = new VerticalPanel( );
		pnlMain.add( pnlPlot );
		pnlMain.add( pnlLegend );
				
		initWidget( pnlMain );
		init( );

		
		// initialise plots list
		clearPlots( );
	}

	/**
	 * Initialise the HTML
	 */
	private void init( ) {
		pnlMain.addStyleName( getCSS( ).mainpanel( ) );
		
		// create the canvas
		canvas = Canvas.createIfSupported( );
		if( canvas != null ) {
			pnlPlot.add( canvas );
		} else {
			pnlPlot.add( new HTMLPanel( Lang.text.W_NoCanvasSupport( ) ) );
		}
		
		// set the size of the canvas
		canvas.setStyleName( "plotPanel" );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	@Override
	public void setPixelSize( int width, int height ) {
		super.setPixelSize( width, height );
		
		this.width = width * PLOTAREA_FACT;
		this.height = height;
		
		// setup canvas size
		canvas.setPixelSize( (int)(width * PLOTAREA_FACT), (int)(height * 0.95) );
		canvas.setCoordinateSpaceHeight( 1000 );
		canvas.setCoordinateSpaceWidth( 1000 );
		
		// and legend size
		pnlLegend.setPixelSize( (int)(width * (1 - PLOTAREA_FACT)), height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
	 * @deprecated Use setPixelSize instead
	 */
	@Override
	@Deprecated
	public void setSize( String width, String height ) {
		super.setSize( width, height );
	}
	
	/**
	 * Sets the player using this widget
	 * 
	 * @param player The player
	 */
	public void setPlayer( Player player ) {
		this.player = player;
	}
	
	/** @return The player using this widget */
	public Player getPlayer( ) { return player; }
	
	/**
	 * @see plangame.gwt.client.widgets.listeners.TTLWidget#setDisplayRelative(boolean)
	 */
	@Override
	public void setDisplayRelative( boolean relative ) {
		this.relative = relative;
		
		// update Y axis caption and style
		options.yaxis.caption = (relative ? Lang.text.W_Timeline_YCaptionRel( ) : Lang.text.W_Timeline_YCaptionAbs( ) );
		options.yaxis.labelstyle = (relative ? Style.Percentage2 : Style.IntK );
		
		if( !resetPlots( ) ) return;
		paint( );		
	}
	
	/** @return True when the plots use relative data */
	public boolean isRelative( ) { return relative; }
	
	/**
	 * Sets whether the network related plots are activated or not
	 * 
	 * @param draw True to enable drawing of network plots
	 */
	public void setDrawNetwork( boolean draw ) {
		final int netidx = (isRelative( ) ? 1 : 2);
		
		setDrawPlots( draw, plots.get( netidx ), plots.get( netidx + 1 ) );
	}
	
	/**
	 * Sets the list of plan tasks that should be marked in the plot are
	 * 
	 * @param marked The list of marked plan tasks
	 */
	public void setMarked( List<PlanTask> marked ) {
		this.marked = marked;
		
		// invalidate the cache and redraw
		plotcache = null;
		paint( );
	}
	
	/**
	 * @return The set of marked plan tasks
	 */
	public List<PlanTask> getMarked( ) { return marked; }
		
	/**
	 * Adds a series of plots at once
	 * 
	 * @param plots The plots to add
	 */
	protected void addPlots( Collection<Plot> plots ) {
		for( Plot p : plots )
			this.plots.add( p );

		// invalidate plot cache and reset legends
		plotcache = null;
		updateLegend( );
	}

	/**
	 * Clears the list of active plots and resets Y boundaries
	 */
	private void clearPlots( ) {
		if( plots == null )
			plots = new ArrayList<Plot>( );
		else
			plots.clear( );
		
		// invalidate caches
		plotcache = null;
		values_y = null;
	}
		
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) {
		super.onSetSelection( oldselection, newselection );
		
		// only update if the selection changed
		if( oldselection == null && newselection == null || (oldselection != null && oldselection.equals( newselection ) ) ) return;
		
		paint( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		if( !resetPlots( ) ) return;

		paint( );
	}

	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// recompute only affected values
		switch( change.getType( ) ) {
			case MethodRemoved:
				// previously planned period
				clearIfSelected( change.getPrevious( ) ); 
				compute( change.getPrevious( ).getPeriod( true ) );			
				break;

			case MethodChanged:
				// clear selection
				clearIfSelected( change.getPrevious( ) );
				// intentional fall-through here

			case MethodMoved:
				// both previous and new
				compute( change.getPrevious( ).getPeriod( true ) );			
				compute( change.getResult( ).getPeriod( true ) );				
				break;
			
				
			case MethodAdded:
				// only the new period
				compute( change.getResult( ).getPeriod( true ) );
				break;
			
			case MethodDelaySet:
				// the delayed period
				compute( change.getResult( ).getPeriodDelayed( ) );

			default:
				break;
		}
		
		// redraw
		paint( );
	}
	
	/**
	 * Updates the plots
	 * 
	 * @return True if updated and ready for draw
	 */
	@SuppressWarnings("serial")
	protected boolean resetPlots( ) {
		// clear all plots
		clearPlots( );
		
		if( !gameDataReady( ) ) return false;		
		
		final List<Plot> newplots = new ArrayList<Plot>( );
		
		// Only add Idle TTL when not displaying relative
		if( !isRelative( ) ) newplots.add( 
			// Idle
			new Plot( new Function( ) {
				@Override public double eval( double x ) {
					return getGameData( ).getData( ).getTTLIdle( new TimePoint( (int)x ) );
				}
			}, "#00ff00", Lang.text.W_Timeline_PlotIdle( ) )
		);
		
		
		// Individual
		newplots.add( 
			new Plot( new Function( ) {
				@Override public double eval( double x ) {
					final TimePoint t = new TimePoint( (int) x );
					
					if( getPlayer( ) == null )
						return getGameData( ).getData( ).getTTLIndividual( t, relative );
					
					return getGameData( ).getData( ).getTTLIndividual( getPlayer(), t, relative );
				}
			}, "yellow", Lang.text.W_Timeline_PlotIndividual( ) )
		);
			
		// Network best case
		newplots.add(
			new Plot( new Function( ) {
				@Override public double eval( double x ) {
					return getGameData( ).getData( ).getTTLNetwork( new TimePoint( (int) x ), relative ).getBestRealised( );
				}
			}, "orange", Lang.text.W_Timeline_PlotNetworkBest( ) )
		);
		
		// Network worst case
		newplots.add(
			new Plot( new Function( ) {
				@Override public double eval( double x ) {
					return getGameData( ).getData( ).getTTLNetwork( new TimePoint( (int) x ), relative ).getWorstRealised( );
					}
			}, "red", Lang.text.W_Timeline_PlotNetworkWorst( ) )
		);
		
		// add all plots
		addPlots( newplots );
		
		// get x values
		setXAxis( getGameData( ).getGamePeriod( ) );
	
		// recompute all y values (this also updates axes)
		values_y = new double[ plots.size( ) ][ values_x.length ];		
		compute( getGameData( ).getGamePeriod( ) );
		
		return true;
	}	
	
	/**
	 * Enables/disables drawing of the selected plots
	 * 
	 * @param draw True  to enable drawing
	 * @param plots The plots to draw or not
	 */
	public void setDrawPlots( boolean draw, Plot... plots ) {
		boolean changed = false;
		for( Plot p : plots )
			changed |= (p.drawn != draw);
		if( !changed ) return;
		
		// change the draw status
		for( Plot p : plots )
			p.drawn = draw;
		
		// check if at least one plot is drawn
		boolean anydrawn = false;
		for( Plot p : this.plots ) anydrawn |= p.drawn;
		
		if( anydrawn ) {
			updateYAxis( );
		} else
			setYAxis( 0, 1 );
				
		plotcache = null;
		updateLegend( );		
		paint( );
	}
	
	/**
	 * Enables/disables plot stacking
	 * 
	 * @param stacked True to draw plots stacked
	 */
	public void setStacked( boolean stacked ) {
		options.stacked = stacked;
		
		updateYAxis( );
		paint( );
	}
	
	/**
	 * @return Whether the plots are drawed stacked
	 */
	public boolean isStacked( ) {
		return options.stacked;
	}
	
	/**
	 * Updates the drawn plots, uses cache if possible
	 */
	protected void paint( ) {
		if( !gameDataReady( ) ) return;
		paintStart( );
		
		// pre compute some drawing constants
		final double w = canvas.getCoordinateSpaceWidth( );
		final double h = canvas.getCoordinateSpaceHeight( );
		final Context2d c = canvas.getContext2d( );
		final JointPlan jplan = getGameData( ).getJointPlan( );
		
		// save drawing context
		c.save( );
		c.scale( 1.0, -1.0 );
		c.translate( 0, -h );

		// clear the panel
		c.clearRect( 0, 0, w, h );
		
		// check if there is a cached plot image available
		if( plotcache == null ) {
			// draw plot axes
			c.setLineWidth( AXES_WIDTH );
			c.beginPath( );
			c.moveTo( left, h - top );
			c.lineTo( left, bottom );
			c.lineTo( w - right, bottom );
			c.lineTo( w - right, h - top );
			c.lineTo( left, h - top );
			c.stroke( );				
			
			// draw marked methods behind graph
			for( PlanTask m : getMarked( ) ) {
				c.save( );
				final String colourreg = "#c0c0c0";
				final String colourdel = "#e0e0e0";
				
				final double x1 = getX( m.getStartTime( ), true );
				final double x2 = getX( m.getEndTime( false ).add( new TimeDuration( 1 ) ), true );
				final double x3 = getX( m.getEndTime( true ).add( new TimeDuration( 1 ) ), true );
				final double y = getY( options.yaxis.min, true );
				final double y2 = getY( options.yaxis.max, true );
				
				c.setFillStyle( colourreg );
				c.fillRect( x1, y, x2 - x1, y2 - y );
				c.setFillStyle( colourdel );
				c.fillRect( x2, y, x3 - x2, y2 - y );
				c.stroke( );
				c.restore( );
			}			
			
			// setup plot drawing
			c.setLineWidth( 1.0 );
			
			// get the plot iteration order
			final int start = (!isStacked( ) ? 0 : plots.size( ) - 1);
			final int end = (!isStacked( ) ? plots.size( ) : -1);
		
			// plot all functions
			for( int i = start; i != end; i += (!isStacked( ) ? 1 : -1) ) {
				if( !plots.get( i ).drawn ) continue;
				
				c.save( );
				c.beginPath( );
				if( !isStacked( ) ) {
					c.setLineWidth( PLOT_WIDTH );
					c.setStrokeStyle( plots.get( i ).colour );
					c.setLineJoin( "round" );
				} else {
					c.setFillStyle( plots.get( i ).colour );
				}
						
				// go over all weeks and plot the data
				boolean first = true;
				for( int j = 0 ; j < values_x.length; j++ ) {	
	
					// get the coordinate corresponding to the coordinate space
					final double X = getX( values_x[ j ], true );
					final double Y;
					if( isStacked( ) ) {
						double ytotal = 0;
						for( int n = 0; n <= i; n++ ) if( plots.get( n ).drawn ) ytotal += values_y[ n ][ j ];
						Y = getY( ytotal, true );
					} else 
						Y = getY( values_y[ i ][ j ], true );
									
					// draw single lines?
					if( !isStacked( ) ) {
						if( first ) {
							c.moveTo( X, Y );
							first = false;
						} else
							c.lineTo( X, Y );
					} else {
						// compute width and height of the bar
						// FIXME this is a bit hacky
						final double barw = getX( values_x[ 1 ], true ) - getX( values_x[ 0 ], true );
						final double barh = Y - bottom;

						// create areas
						final double BAR_SPACE = 0;
						c.fillRect( X + BAR_SPACE, bottom + AXES_WIDTH, barw - BAR_SPACE * 2, barh - AXES_WIDTH );						
					}
				}
				
				// draw the plot
				c.stroke( );
				c.restore( );
			}
			
			// draw tick markers over graph
			c.setLineWidth( AXES_WIDTH / 2.0 );
			c.beginPath( );
			for( TimePoint t : jplan.getPeriod( ).toWeeks( ) ) {
				c.moveTo( getX( t, true ), bottom );
				c.lineTo( getX( t, true ), bottom + (int)(h / 100) );
			}
			c.stroke( );
			
			
			// cache the plots
			plotcache = c.getImageData( 0, 0, w, h );
		} else {
			c.putImageData( plotcache, 0, 0 );
		}
		
		// if there is a method selected, draw lines where it is in the plot
		final PlanTask selected = getSelected( );
		if( selected != null ) {
			final String colour = "#0000ff";
			drawVerLine( getX( selected.getStartTime( ), true ), colour, TASK_WIDTH );    			
			drawVerLine( getX( selected.getEndTime( false ).add( new TimeDuration( 1 ) ), true ), colour, TASK_WIDTH );    			
			drawVerLine( getX( selected.getEndTime( true ).add( new TimeDuration( 1 ) ), true ), colour, TASK_WIDTH );    			
		}

		// restore drawing context
		c.restore( );
		
		paintEnd( );
	}
	
	/**
	 * Draws a vertical line at the given x position
	 *
	 * @param x The x position
	 * @param colour The CSS colour
	 * @param width The line width
	 */
	private void drawVerLine( double x, String colour, double witdh ) {
		final Context2d c = canvas.getContext2d( );
		c.save( );
		
		// setup drawing
		c.beginPath( );
		c.setStrokeStyle( colour );
		c.setLineWidth( witdh );
		
		// draw the line
		c.moveTo( x, getY( options.yaxis.min, true ) );
		c.lineTo( x, getY( options.yaxis.max, true ) );
		c.stroke( );
		
		c.restore( );
	}
	
	/**
	 * Computes the X coordinate that corresponds to the given week
	 * 
	 * @param week The week number
	 * @param coordspace True to get the X value in the coordinate space
	 * @return The X value for the week, relative or absolute in the coordinate space
	 */
	private double getX( TimePoint week, boolean coordspace ) {
		// get the relative position of the week
		final double X = options.xaxis.getCoord( week.getWeek( ) );
		return (coordspace ? (X * (canvas.getCoordinateSpaceWidth( ) - left - right) + left) : X );
	}
	
	/**
	 * Computes the real X that corresponds to the specified coordinate space X
	 * 
	 * @param x The x value
	 * @return The real x value
	 */
	protected double getRealX( double x ) {
		return x * (width / canvas.getCoordinateSpaceWidth( ));
	}
	
	/**
	 * Computes the Y coordinate in coordinate space of the canvas that
	 * corresponds to the given value
	 * 
	 * @param value The Y value
	 * @param coordspace True to compute the absolute coordinate in the coordinate space
	 * @return The corresponding Y coordinate in the graph area
	 */
	private double getY( double value, boolean coordspace ) {
		// compute the Y coordinate within the Y limits
		final double Y = options.yaxis.getCoord( value );

		// return the point in the graph area that corresponds to this Y value
		return (coordspace ? (Y * (canvas.getCoordinateSpaceHeight( ) - top - bottom) + bottom) : Y);
	}
	
	/**
	 * Computes the real Y that corresponds to the specified coordinate space Y
	 * 
	 * @param y The y value
	 * @return The real y value
	 */
	protected double getRealY( double y ) {
		return y * (height / canvas.getCoordinateSpaceHeight( ));
	}
	
	
	/**
	 * Recomputes (part of) the values to plot for all functions
	 *
	 * @param span The span to recompute
	 */
	private void compute( TimeSpan span ) {
		// clear the cache on any recompute
		plotcache = null;
		
		// recompute values		
		for( TimePoint t : span.toWeeks( ) )
			for( int i = 0; i < plots.size( ); i++ ) {
				final double y = plots.get( i ).function.eval( t.getWeek( ) );
				values_y[ i ][ t.getWeek( ) ] = y;
			}
		
		// update y limits to be sure
		updateYAxis( );
	}
	
	/**
	 * Recomputes the y limits
	 */
	private void updateYAxis( ) {
		// go over entire function again to find Y limits
		double ymin = Double.MAX_VALUE;
		double ymax = Double.MIN_VALUE;
		
		if( values_x == null ) return;
		
		for( int t = 0; t < values_x.length; t++ ) {
			// compute stacked y or single
			if( isStacked( ) ) {
				for( int i = 0; i < plots.size( ); i++ ) {
					if( !plots.get( i ).drawn ) continue;
					double y = 0;

					for( int j = 0; j <= i; j++ ) 
						if( plots.get( j ).drawn )
							y += values_y[ j ][ t ];
					
					if( y < ymin ) ymin = y;
					if( y > ymax ) ymax = y;
				}
			} else {		
				for( int i = 0; i < plots.size( ); i++ ) {
					if( !plots.get( i ).drawn ) continue;
					
					final double y = values_y[ i ][ t ];
					
					if( y < ymin ) ymin = y;
					if( y > ymax ) ymax = y;
				}				
			}
		}
		
		// set the new Y axis
		setYAxis( ymin, ymax );
	}
	
	/**
	 * Sets the new x axis, adds axis labels
	 * 
	 * @param span The span that is covered on the X axis
	 */
	public void setXAxis( TimeSpan span ) {
		// set values and limits
		values_x = span.add( new TimeDuration( 1 ) ).toWeeks( ).toArray( new TimePoint[0] );
		options.xaxis.setLimits( values_x[ 0 ].getWeek( ), values_x[ values_x.length - 1 ].getWeek( ) );
		
		// set the caption
		options.xaxis.caption = Lang.text.W_Timeline_XCaption( );
		
		// clear previous x labels
		if( xlabels == null ) xlabels = new ArrayList<Label>( );
		for( Label lbl : xlabels ) pnlPlot.remove( lbl );
		xlabels.clear( );
		
		// create new labels
		for( Double val : options.xaxis.getTicks( ) ) {
			final Label l = new Label( Format.f( val + 1, options.xaxis.labelstyle ) );
			l.addStyleName( getCSS( ).xaxis_label( ) );
			l.setWidth( XLABEL_WIDTH + "px" );
			
			// add and position the label
			pnlPlot.add( l );
			final int tickX = (int)(getX( new TimePoint( val.intValue( ) ), false ) * (width - getRealX( left + right )) + getRealX( left) );
			final int tickY = (int) (height + XLABEL_YOFFSET);
			pnlPlot.setWidgetPosition( l, tickX, tickY );
			
			// add it to the labels
			xlabels.add( l );
		}
		
		// add caption label
		if( xcaption != null ) pnlPlot.remove( xcaption );
		xcaption = new Label( options.xaxis.caption );
		xcaption.addStyleName( getCSS( ).xaxis_caption( ) );
		pnlPlot.add( xcaption );
		
		// move it to the right position
		final int X = (int)((width - getRealX( left + right ) - xcaption.getOffsetWidth( ) - XLABEL_MIN) / 2 + getRealX( left ));
		final int Y = (int)(height + XCAPTION_YOFFSET);
		pnlPlot.setWidgetPosition( xcaption, X, Y );
	}
	
	/**
	 * Sets the new y axis, adds new axis labels
	 * 
	 * @param ymin The new minimal y value
	 * @param ymax The new maximal y value
	 */
	private void setYAxis( double ymin, double ymax ) {
		options.yaxis.setLimits( Math.min( 0d, Math.min( ymin, ymax ) ), Math.max( ymin, ymax ) );
		
		// add caption label
		if( ycaption != null ) pnlPlot.remove( ycaption );
		ycaption = new Label( options.yaxis.caption );
		ycaption.addStyleName( getCSS( ).yaxis_caption( ) );
		pnlPlot.add( ycaption );
		
		// move it to the right position
		// X is a bit hacky, but the X position depends on the label size and
		// getOffsetWidth returns 0
		final int X = (int) (-2.4 * options.yaxis.caption.length( )); 
		final int Y = (int)(( height - getRealY( top + bottom ) - YLABEL_MIN ) / 2 + getRealY( top ));
		pnlPlot.setWidgetPosition( ycaption, X, Y );		
		
		// clear old labels
		if( ylabels == null ) ylabels = new ArrayList<Label>( );
		for( Label lbl : ylabels )
			pnlPlot.remove( lbl );
		ylabels.clear( );
		
		// create new labels
		for( Double val : options.yaxis.getTicks( ) ) {
			final Label l = new Label( Format.f( val, options.yaxis.labelstyle ) );
			l.addStyleName( getCSS( ).yaxis_label( ) );
			l.setWidth( YLABEL_WIDTH + "px" );
			
			// add and position the label
			pnlPlot.add( l );
			final int tickX = (int)YLABEL_XOFFSET;
			final int tickY = (int)(((1.0 - getY( val, false )) * (height - getRealY( bottom + top ) - YLABEL_MIN)) + getRealY( top ) );
			pnlPlot.setWidgetPosition( l, tickX, tickY );
			
			// add it to the labels
			ylabels.add( l );
		}
	}
	
	/**
	 * Updates the legend
	 */
	private void updateLegend( ) {
		// clear current legend entries
		pnlLegend.clear( );
		
		// add all entries
		for( final Plot p : plots ) {
			// create a legend entry
			final HorizontalPanel leg = new HorizontalPanel( );
			leg.addStyleName( getCSS( ).legend_entry( ) );
			
			// colour label
			final Label l = new Label( "" );
			l.addStyleName( getCSS( ).legend_colour( ) );
			l.getElement( ).getStyle( ).setBackgroundColor( p.colour );			
			leg.add( l );
			
			// and legend text
			final HyperLabel hp = new HyperLabel( p.legend );
			hp.addStyleName( getCSS( ).legend_text( ) );
			hp.setHoverable( true );
			hp.setStyleName( getCSS( ).legend_text_notdrawn( ), !p.drawn );
			
			hp.addClickHandler( new ClickHandler( ) {
				@Override public void onClick( ClickEvent event ) {
					setDrawPlots( !p.drawn, p );
				}
			} );
			
			leg.add( hp );
			
			pnlLegend.add( leg );
		}
	}
	
	/**
	 * @return The widget's CSS
	 */
	protected TTLTimelineWidgetCSS getCSS( ) {
		return ServiceProvider.getInstance( ).getResources( ).plotcss( );
	}
	
	/**
	 * Private class that describes one plot
	 */
	private class Plot {
		/** The function to plot */
		protected final Function function;
		
		/** The colour (CSS) to plot it in */
		protected final String colour;
		
		/** The legend text */
		protected final String legend;
		
		/** Whether the plot is drawn or not */
		protected boolean drawn;
		
		/**
		 * Creates a new plot
		 * 
		 * @param function The function to plot
		 * @param colour The colour to plot it in
		 * @param legend The legend text
		 */
		protected Plot( Function function, String colour, String legend ) { 
			this.function = function;
			this.colour = colour;
			this.legend = legend;
			this.drawn = true;
		}
	}
	
	/**
	 * Private class that contains all plot options
	 */
	private class PlotOptions {
		/** Plot graphs stacked on top of each other */
		protected boolean stacked;
		
		/** X axis */
		protected Axis xaxis;
		
		/** Y axis */
		protected Axis yaxis;

		/**
		 * Creates a new PlotOptions objects with defaults
		 */
		public PlotOptions( ) {
			// setup initial axes
			xaxis = new Axis( );
			xaxis.setTicks( 5, false );
			yaxis = new Axis( );
			yaxis.labelstyle = Style.IntK;
			yaxis.setTicks( 4, true );
			
			stacked = true;
		}
		
		/** Axis properties */
		private class Axis {
			/** Tick interval */
			private double tickInterval;
			
			/** Number of ticks */
			private int tickNum;
			
			/** Caption */
			private String caption;
			
			/** Axis min value */
			private double min;
			
			/** Axis max value */
			private double max;
			
			/** Axis label style format */
			private Style labelstyle;
			
			/**
			 * Creates a new axis object
			 */
			protected Axis( ) {
				caption = "";
				labelstyle = Style.Int;
				
				min = 0;
				max = 1;
				
				setTicks( 2, true );
			}

			/**
			 * Sets the tick / number of labels property
			 * 
			 * @param value The property value
			 * @param fixedNum True to set the value as a fixed number, false for value per tick
			 */
			protected void setTicks( double value, boolean fixedNum ) {
				tickInterval = (fixedNum ? -1 : value);
				tickNum = (int) (fixedNum ? value : -1);
			}
			
			/**
			 * @return The number of ticks, based on the axis settings
			 */
			protected int getTickCount( ) {
				if( tickNum != -1 ) return tickNum;
				
				// not fixed, compute the number of ticks
				return (int) ((max - min) / tickInterval + 1);
			}
			
			/**
			 * @return The interval per tick, based on the axis settings
			 */
			protected double getTickInterval( ) {
				if( tickInterval != -1 ) return tickInterval;
				
				// fixed tick number, compute interval
				return (max - min) / (tickNum - 1);
			}
			
			/**
			 * Computes the tick values and returns the set of ticks on this axis
			 * 
			 * @return The ticks as a double array
			 */
			protected double[] getTicks( ) {
				final double[] ticks = new double[ getTickCount( ) ];
				for( int i = 0; i < ticks.length; i++ )
					ticks[ i ] = getTickInterval( ) * i;
				
				return ticks;
			}
			
			/**
			 * Sets the axis limits
			 * 
			 * @param min The minimal value
			 * @param max The maximal value
			 */
			protected void setLimits( double min, double max ) {
				this.min = Math.min( min, max );
				this.max = Math.max( min, max );
			}
			
			/**
			 * Computes the value's coordinate relative to the x limits
			 * 
			 * @param value The value
			 * @return The relative position [0, 1] of the coordinate on the axis
			 */
			protected double getCoord( double value ) {
				return (value + min) / (max - min);
			}
		}
	}
}
