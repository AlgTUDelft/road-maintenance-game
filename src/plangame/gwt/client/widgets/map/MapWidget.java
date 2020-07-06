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
package plangame.gwt.client.widgets.map;

import plangame.game.plans.PlanTask;
import plangame.gwt.client.gamedata.GameInfoUpdateListener;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.shared.state.GameInfo;
import plangame.model.infra.assets.Asset;
import plangame.model.infra.graph.BoundingBox;
import plangame.model.infra.graph.Edge;
import plangame.model.infra.graph.InfraElement;
import plangame.model.infra.graph.InfraGraph;
import plangame.model.infra.graph.Node;
import plangame.model.infra.graph.Path;
import plangame.model.tasks.Portfolio;
import plangame.model.traffic.TTLDistribution;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * Widget for display of the infrastructure map
 * 
 * @author Joris Scharpff
 */
public class MapWidget extends BasicWidget implements GameInfoUpdateListener {
	/** The focus panel containing the canvas for mouse events */
	protected FocusPanel fcMap;
	
	/** The Canvas widget */
	protected Canvas canvas;
	
	/** The graph bounding box (updated before paint) */
	protected BoundingBox bbox;
	
	/** The TTL distribution that is drawn (null if none is drawn) */
	protected TTLInfo ttlinfo;
	
	/** Stored network image */
	protected ImageData emptyimage;
	
	/** True to enable display of TTL distribution on map */
	protected boolean showDistribution;
	
	// Constants
	private final static int LINE_WIDTH_ASSIGNED = 15;
	private final static int LINE_WIDTH_NOTASSIGNED = 2;
	private final static int NODE_SIZE = 2;
	private final static String COLOUR_NOTASSIGNED = "black";
	private final static String COLOUR_SELECTED = "#0000cc";
	
	
	/**
	 * Creates a new plan view widget
	 */
	public MapWidget( ) {
		super( );
		
		setDisplayDistribution( false );
		invalidate( );
				
		// create the canvas (can be null if not supported)
		canvas = Canvas.createIfSupported( );
		if( canvas == null ) {
			initWidget( new HTMLPanel( Lang.text.W_NoCanvasSupport( ) ) );			
			return;
		}
		
		// create focus panel and add the canvas
		fcMap = new FocusPanel( canvas );

		// set the size of the canvas
		canvas.setStyleName( "mapPanel" );
		canvas.setCoordinateSpaceHeight( 500 );
		canvas.setCoordinateSpaceWidth( 500 );
		
		initWidget( fcMap );
	}

	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
	 */
	@Override
	public void setSize( String width, String height ) {
		// clear cached image
		invalidate( );
		
		super.setSize( width, height );
		canvas.setSize( width, height );
	}
	
	/**
	 * Sets whether the TTL distribution is displayed on the map on selection of
	 * tasks
	 * 
	 * @param show True to show the distribution
	 */
	public void setDisplayDistribution( boolean show ) {
		final boolean prevvalue = showDistribution;
		this.showDistribution = show;
		
		if( prevvalue != show ) {
			invalidate( );
			paint( );
		}
	}
	
	/**
	 * @return True if the TTL distribution should be shown on the map
	 */
	public boolean showTTLDistribution( ) {
		return showDistribution;
	}
	
	/**
	 * Clears the current TTL distribution so that it is not drawn any more
	 */
	public void clearTTLDistribution( ) {
		if( ttlinfo != null )
			invalidate( );
		
		ttlinfo = null;
	}

	/**
	 * Sets the TTL distribution that is to be displayed on this map widget.
	 * 
	 * @param ttldist The TTL distribution to draw
	 **/
	public void setTTLDistribution( TTLDistribution ttldist ) {
		clearTTLDistribution( );
		ttlinfo = new TTLInfo( ttldist );
		
		invalidate( );
		paint( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.GameInfoUpdateListener#onGameInfoSet(plangame.gwt.shared.state.GameInfo)
	 */
	@Override
	public void onGameInfoSet( GameInfo gameinfo ) {
		invalidate( );
		
		paint( );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) {
		super.onSetSelection( oldselection, newselection );
		
		paint( );
	}
	
	/**
	 * Invalidates the cached image
	 */
	private void invalidate( ) {
		emptyimage = null;
	}
	
	/**
	 * Draws the map
	 */
	public void paint( ) {
		// check if there is information about the graph
		if( !gameDataReady( ) ) return;
		paintStart( );
		
		final InfraGraph graph = getGameData( ).getGameInfo( ).getInfra( ).getGraph( );
		
		// make sure we have the right bounding box
		bbox = graph.getBoundingBox( );
		
		// pre compute some drawing constants
		final double w = canvas.getCoordinateSpaceWidth( );
		final double h = canvas.getCoordinateSpaceHeight( );
		final Context2d c = canvas.getContext2d( );
		
		// clear the canvas
		c.save( );
		c.clearRect( 0, 0, w, h );
		
		// only repaint network if no current empty image is available
		if( emptyimage == null ) {
			// draw the graph paths
			for( Path p : graph.getPaths( ) ) {
				final DrawOptions options = new DrawOptions( );
				if( showTTLDistribution( ) && ttlinfo != null && p.getAsset( ) != null ) {
					options.linewidth = 5;
					options.colour = ttlinfo.getColour( p.getAsset( ) );
				} else {
					// set default draw options
					options.linewidth = LINE_WIDTH_NOTASSIGNED;
					options.colour = COLOUR_NOTASSIGNED;
					
					// if assigned, change the draw options
					for( Portfolio pf : getGameData( ).getGameInfo( ).getPortfolios( ) ) {
						if( pf.isServiced( p.getAsset( ) ) ) {
							options.linewidth = LINE_WIDTH_ASSIGNED;
							options.colour = pf.getColour( );
						}
					}
				}
				
				drawPath( p, c, options );
			}
			
			// store the empty network image
			emptyimage = c.getImageData( 0, 0, canvas.getCoordinateSpaceWidth( ), canvas.getCoordinateSpaceWidth( ) );
		} else {
			c.putImageData( emptyimage, 0, 0 );
		}
		
		// draw selected item
		if( getSelected( ) != null ) {
			final InfraElement e = getSelected( ).getAsset( ).getInfraElement( );
			if( e != null ) {
				final DrawOptions op = new DrawOptions( );
				// re-enable this if we want TTL dependent colouring for the selected segment
				// if( ttlinfo != null ) op.colour = ttlinfo.getColour( selected.getAsset( ) );
				op.colour = COLOUR_SELECTED;
				op.linewidth = LINE_WIDTH_ASSIGNED;
				drawElement( e, c, op );
			}
		}
		
		// draw the nodes over the paths
		for( Node n : graph.getNodes( ) ) {
			final DrawOptions d = new DrawOptions( );
			d.linewidth = NODE_SIZE;
			drawNode( n, c, d );
		}
		
		// restore the drawing context
		c.restore( );
		paintEnd( );
	}

	/**
	 * Determines the type of element to draw and calls the correct draw function
	 * 
	 * @param elem The element to draw
	 * @param c The drawing context
	 * @param options The draw options
	 */
	private void drawElement( InfraElement elem, Context2d c, DrawOptions options ) {
		if( elem instanceof Node ) {
			drawNode( (Node)elem, c, options );
		} else if( elem instanceof Edge ) {
			drawEdge( (Edge)elem, c, options );
		} else if( elem instanceof Path ) {
			drawPath( (Path)elem, c, options );
		} else {
			assert false : "Unknown element in MapWidget";
		}
	}

	/**
	 * Draws the node using the current context
	 * 
	 * @param node The node to draw
	 * @param c The context
	 * @param options The draw options 
	 */
	private void drawNode( Node node, Context2d c, DrawOptions options ) {
		// draw a little circle for the node
		c.beginPath( );
		options.apply( c );
		c.arc( getX( node.getX( ) ), getY( node.getY( ) ), options.linewidth + 1, 0, 2 * Math.PI );
		c.fill( );
		c.stroke( );
	}
	
	/**
	 * Draws the edge using the current context
	 * 
	 * @param edge The edge to draw
	 * @param c The context
	 * @param options The draw options
	 */
	private void drawEdge( Edge edge, Context2d c, DrawOptions options ) {
		// move to origin and draw a line to destination
		c.beginPath( );
		options.apply( c );
		c.moveTo( getX( edge.getOrigin( ).getX( ) ), getY( edge.getOrigin( ).getY( ) ) );
		c.lineTo( getX( edge.getDestination( ).getX( ) ), getY( edge.getDestination( ).getY( ) ) );
		c.stroke( );		
	}
	
	/**
	 * Draws the path using the current context
	 * 
	 * @param path The path to draw
	 * @param c The drawing context
	 * @param options The draw options
	 */
	private void drawPath( Path path, Context2d c, DrawOptions options ) {
		// just draw all its edges
		for( Edge e : path.getEdges( ) )
			drawEdge( e, c, options );
	}
	
	/**
	 * Computes the X coordinate on the canvas that corresponds to the world X
	 * 
	 * @param X The world X
	 * @return The corresponding canvas X coordinate
	 */
	private double getX( double X ) {
		return bbox.getX( X ) * canvas.getCoordinateSpaceWidth( );
	}
	
	/**
	 * Computes the Y coordinate on the canvas that corresponds to the world Y
	 * 
	 * @param Y The world Y
	 * @return The corresponding canvas Y coordinate
	 */
	private double getY( double Y ) {
		return bbox.getY( Y ) * canvas.getCoordinateSpaceHeight( );
	}

	/**
	 * TTL info for drawing, stores this info for quick drawing
	 */
	private static class TTLInfo {
		/** The upper bound of discrete TTL increase levels */
		protected double[] catmax;
		
		/** The number of categories to use */
		private final static int CATEGORIES = 6;
		
		/** The category colours */
		protected String[] catcolours;
		
		/** The network TTL to show */
		protected TTLDistribution networkttl;
		
		/**
		 * Creates a new TTL info
		 * 
		 * @param networkttl The network TTL to display
		 */
		protected TTLInfo( TTLDistribution networkttl ) {
			this.networkttl = networkttl;
			
			// check if any network TTL is known
			if( networkttl.getRange( ) < 0.001 ) {
				// setup one dummy category
				catmax = new double[ ] { 0 };
				catcolours = new String[ ] { "#00ff00" };
				return;
			}
			
			// divide differences in discrete categories, first and last categories
			// include every smaller and larger TTL than the minimum and maximum on
			// the network respectively
			final double low = networkttl.getTTL( networkttl.getLowest( ) );
			final double catsize = (networkttl.getTTL( networkttl.getHighest( ) ) - low) / CATEGORIES;
			catmax = new double[ CATEGORIES - 1 ];
			for( int i = 0; i < catmax.length; i++ ) {
				catmax[ i ] = low + catsize * (i + 1);
			}
			
			// compute category colours
			final int half = CATEGORIES / 2;
			catcolours = new String[ CATEGORIES ];
			double inc = 255 / (half - 1); 
			for( int i = 0; i < half; i++ )
				catcolours[ i ] = CssColor.make( (int)(inc * i), 255, 0 ).value( );
			
			inc = 256 / (CATEGORIES - half - 1);
			for( int i = half; i < CATEGORIES; i++ )
				catcolours[ i ] = CssColor.make( 255, 255 - (int)(inc * (i - half)), 0 ).value( );
		}
		
		/**
		 * Retrieves the colour that corresponds to the category to which this
		 * asset's TTL increase belongs
		 * 
		 * @param asset The asset
		 * @return The colour as CSS code
		 */
		protected String getColour( Asset asset ) {
			// get asset TTL increase
			final Double ttl = networkttl.getTTL( asset );
			
			// get the category number of this TTL increase
			return catcolours[ getCategory( ttl ) ];
		}
		
		/**
		 * Computes the category number corresponding to the TTL increase. All TTL
		 * values lower than the first category are included in the first, all TTL
		 * values higher than the last are included in the last
		 * 
		 * @param ttl The TTL value
		 * @return The category number (base 0)
		 */
		protected int getCategory( double ttl ) {
			for( int i = catmax.length - 1; i > 0; i-- )
				if( ttl > catmax[ i ] ) return i;
			
			// not in any category, return lower bound
			return 0;
		}
	}
	
	/**
	 * Element drawing options
	 */
	private static class DrawOptions {
		/** The line width / dot size */
		protected double linewidth;
		
		/** The draw colour CSS code */
		protected String colour;
		
		/**
		 * Creates a new DrawOptions class with default settings
		 */
		protected DrawOptions( ) {
			linewidth = 1.0;
			colour = "black";
		}
		
		/**
		 * Applies the draw options to the current context
		 * 
		 * @param context The 2D drawing context
		 */
		protected void apply( Context2d context ) {
			context.setLineWidth( linewidth );
			context.setStrokeStyle( colour );
		}
	}
}
