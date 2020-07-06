/**
 * @file GameTraceUI.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2014 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         23 dec. 2014
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import plangame.game.Game;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.GameData;
import plangame.gwt.client.gamedata.TotalScore;
import plangame.gwt.server.gametrace.GameTraceException;
import plangame.gwt.server.gametrace.GameTraceReader;
import plangame.gwt.server.gametrace.ProfileScoreResult;
import plangame.gwt.server.gametrace.ProfileScores;
import plangame.gwt.server.gametrace.TraceMsg.TraceType;

/**
 * Simple user interface to go through traces
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GameTraceUI extends JFrame {
	/** The game trace reader used in this UI */
	protected GameTraceReader tracer;
	
	/** The WebRoot directory for game files */
	protected String webroot; // TODO setter
	
	/** The window title */
	private static final String WINDOW_TITLE = "Game Tracer";

	/** UI components */
	/** Score text field */
	private JTextArea txtScore;
	/** Step output console */
	private JLabel lblLog;
	/** Next step button */
	private JButton btnStep;
	/** Reset button */
	private JButton btnReset;
	
	/**
	 * Creates a new UI for game traces
	 * 
	 * @param tracer The game trace reader
	 */
	public GameTraceUI( ) {
		super( WINDOW_TITLE  );
		
		this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		this.addWindowListener( new WindowAdapter( ) {
			@Override public void windowClosing(WindowEvent e) { menuFileExit( ); }
		} );
		this.setSize( 800, 600 );
		this.setLocationRelativeTo( null );
		
		// set defaults
		tracer = null;
		webroot = ".";
		
		// build UI
		init( );
	}
	
	/**
	 * Initialises the UI
	 */
	private void init( ) {	
		initMenus( );
		
		// create simple UI
		final JSplitPane p = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		p.setDividerSize( 0 );
		
		// score label
		txtScore = new JTextArea( "" );
		//txtScore.setEditable( false );
		txtScore.setFont( new Font( Font.MONOSPACED, Font.PLAIN, 12 ) );
		txtScore.setMargin( new Insets( 3, 3, 3, 3 ) );
		txtScore.setPreferredSize( new Dimension( -1, 500 ) );
		p.setTopComponent( txtScore );
		
		// control pane
		final JPanel ctr = new JPanel( );
		lblLog = new JLabel( "Log" );
		lblLog.setPreferredSize( new Dimension( 600, 16 ) );
		lblLog.setHorizontalAlignment( SwingConstants.LEFT );
		ctr.add( lblLog );
		btnReset = new JButton( "Reset" );
		ctr.add( btnReset	);
		btnStep = new JButton( "Step" );
		ctr.add( btnStep );
		p.setBottomComponent( ctr );
		
		// set the panel
		add( p );
		
		// setup action listeners
		initActions( );
	}
	
	/**
	 * Initialise action listeners
	 */
	private void initActions( ) {
		btnReset.addActionListener( new ActionListener( ) {
			@Override	public void actionPerformed( ActionEvent arg0 ) {
				gameReset( );
			}
		} );
		
		btnStep.addActionListener( new ActionListener( ) {
			@Override	public void actionPerformed( ActionEvent e ) {
				gameStep( );
			}
		} );
	}
	
	/**
	 * Initialises the menus
	 */
	private void initMenus( ) {
		final JMenuBar bar = new JMenuBar( );
		
		// File menu
		final JMenu file = new JMenu( "File" );
		final JMenuItem fileOpen = new JMenuItem( "Open" );
		fileOpen.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK ) );
		fileOpen.addActionListener( new ActionListener( ) {
			@Override	public void actionPerformed( ActionEvent arg0 ) { menuFileOpen( ); }
		} );
		file.add( fileOpen );
		final JMenuItem fileExit = new JMenuItem( "Exit" );
		fileExit.addActionListener( new ActionListener( ) {			
			@Override	public void actionPerformed( ActionEvent arg0 ) { menuFileExit( ); }
		} );
		file.add( fileExit );
		bar.add( file );
		
		// game menu
		final JMenu game = new JMenu( "Game" );
		final JMenuItem gameStep = new JMenuItem( "Step" );
		gameStep.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { gameStep( ); }
		} );
		game.add( gameStep );
		final JMenuItem gameToStart = new JMenuItem( "Step to start" );
		gameToStart.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) );
		gameToStart.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { gameStepStart( ); }
		} );
		game.add( gameToStart );
		final JMenuItem gameStepRound = new JMenuItem( "Step to end of round" );
		gameStepRound.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK ) );
		gameStepRound.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { gameStepRound( ); }
		} );
		game.add( gameStepRound );
		final JMenuItem gameStepAll = new JMenuItem( "Step all" );
		gameStepAll.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { gameStepAll( ); }
		} );
		game.add( gameStepAll );
		final JMenuItem gameReset = new JMenuItem( "Reset" );
		gameReset.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { gameReset( ); }
		} );
		game.add( gameReset );
		bar.add( game );
		
		// output menu
		final JMenu out = new JMenu( "Output" );
		final JMenuItem outScore = new JMenuItem( "Scores" );
		outScore.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { updateScore( ); }
		} );
		out.add( outScore );
		final JMenuItem outProfScore = new JMenuItem( "Profile Scores" );
		outProfScore.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK ) );
		outProfScore.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { outProfileScore( ); }
		} );
		out.add( outProfScore );
		bar.add( out );
		
		// trace menu
		final JMenu trace = new JMenu( "Trace" );
		final JMenuItem traceLast = new JMenuItem( "Show last step" );
		traceLast.addActionListener( new ActionListener( ) {
			@Override public void actionPerformed( ActionEvent e ) { traceLast( ); }
		} );
		trace.add( traceLast );
		bar.add( trace );
		
		this.setJMenuBar( bar );
	}
	
	/**
	 * Sets the web root of the game files
	 * 
	 * @param root The root directory path
	 */
	public void setWebRoot( String root ) {
		this.webroot = root;
		
		if( tracer != null )
			tracer.setWebRoot( root );
	}
	
	/**
	 * Reads the specified trace file, creates a traces and shows it in the UI
	 * 
	 * @param tracefile The trace file to read
	 * @throws IOException
	 * @throws GameTraceException if the trace contains invalid mesages
	 */
	public void readTraceFile( String tracefile ) throws IOException, GameTraceException {
		// create a tracer
		final GameTraceReader tr = new GameTraceReader( tracefile );
		tr.setWebRoot( webroot );
		
		// set the tracer in the interface
		this.tracer = tr;
		setTitle( WINDOW_TITLE + " - " + tracer.getTraceFile( ).getName( ) );
		
		// update the interface elements
		lblLog.setText( "Successfully parsed trace file: " + tracefile );
		updateScore( );
	}
	
	/**
	 * Window to open a trace file
	 */
	protected void menuFileOpen( ) {
		// create open file dialog
		final JFileChooser f = new JFileChooser( "C:\\Users\\Joris\\Desktop\\Almende\\gamesessions" );
		if( f.showOpenDialog( this ) != JFileChooser.APPROVE_OPTION ) return;
		
		// read the trace
		try {
			readTraceFile( f.getSelectedFile( ).getPath( ) );
		} catch( Exception e ) {
			JOptionPane.showMessageDialog( this, "Error while reading trace file: " + e.getMessage( ) );
		}
	}
	
	/**
	 * Exits the UI
	 */
	protected void menuFileExit( ) {
		this.setVisible( false );
		System.exit( 0 );
	}
	
	/**
	 * Resets the game
	 */
	protected void gameReset( ) {
		try {
			tracer.reset( );
			txtScore.setText( "" );
			lblLog.setText( "Trace has been reset" );
		} catch( GameTraceException e ) {
			lblLog.setText( "Exception while resetting: " + e.getMessage( ) );
		}
	}
	
	/**
	 * Performs a single step of the game
	 */
	protected void gameStep( ) {
		try {
			final String step = tracer.step( );
			if( step == null )
				lblLog.setText( "The game has ended" );
			else
				lblLog.setText( step );
			
			// update the score display
			updateScore( );
		} catch( GameTraceException gte ) {
			lblLog.setText( gte.getMessage( ) );
		}		
	}
	
	
	/**
	 * Performs steps until start of the game
	 */
	protected void gameStepStart( ) {
		try {
			tracer.reset( );
			lblLog.setText( tracer.stepTo( TraceType.GameStarted ) );
			
			// updarte the score display
			updateScore( );
		} catch( GameTraceException gte ) {
			lblLog.setText( gte.getMessage( ) );
		}		
	}
	
	/**
	 * Performs all steps until the end of the game
	 */
	protected void gameStepAll( ) {
		try {
			String msg = "";
			while( (msg = tracer.step( )) != null ) {
				lblLog.setText( msg );
			}
			updateScore( );
		} catch( GameTraceException gte ) {
			lblLog.setText( gte.getMessage( ) );
		}
	}
	
	/**
	 * Performs all steps until the end of the plan round
	 */
	protected void gameStepRound( ) {
		try {
			lblLog.setText( tracer.stepTo( TraceType.SubmittedAll, false ) );
			
			// update the score display
			updateScore( );
		} catch( GameTraceException gte ) {
			lblLog.setText( gte.getMessage( ) );
		}		
	}
	
	/**
	 * Outputs the profile scores to the console of the current joint plan
	 */
	protected void outProfileScore( ) {
		if( tracer == null ) {
			lblLog.setText( "No game loaded" );
			return;
		}
		
		try {
			// get the scores
			final ProfileScores ps = new ProfileScores( );
			final Map<Player, ProfileScoreResult> scores = ps.getScore( tracer.getJointPlan( ) );
			
			// output them per player (in order of portfolio)
			final List<Player> players = sortOnPortfolio( scores.keySet( ) );
			String scoretext = "";
			for( Player p : players ) {
				//System.out.print( pad( p + " (" + p.getPortfolio( ) + ")", 20, true ) + ": " +  );
				final String sc = scores.get( p ).toString( ).replace( " ", "\t" ).replace( ".", "," );
				scoretext += sc + "\n";
			}
			
			txtScore.setText( scoretext );
			
		} catch( GameTraceException e ) {
			lblLog.setText( e.getMessage( ) );
		}
	}
	
	/**
	 * Shows the last performed trace step	
	 */
	protected void traceLast( ) {
		if( tracer == null ) {
			lblLog.setText( "No game is being traced" );
			return;
		}
		
		JOptionPane.showMessageDialog( this, tracer.getLastStep( ) );
	}
	
	/**
	 * Updates the score field
	 */
	protected void updateScore( ) {
		// get the game
		final Game game;
		try {
			game = tracer.getGame( );
		} catch( GameTraceException gte ) {
			txtScore.setText( "No active game running" );
			return;
		}
		
		// format the current game score
		txtScore.setText( formatScore( game ) );
	}
	
	/**
	 * Formats the current game's score and sets it in the textbox
	 */
	private String formatScore( Game game ) {
		final int colsize = 30;
		String txt = "";
		
		// column headers
		txt += pad( "Players", colsize, true ) + "|";
		txt += pad( "Profits", colsize, true ) + "|";
		txt += pad( "TTL", colsize, true ) + "\n";
		txt += pad( "", (colsize + 1) * 3, true, '-' ) + "\n";
		
		// get the game data
		GameData data = null;
		try {
			data = tracer.getGameData( );
		} catch( GameTraceException e ) { /** do nothing here */ }
		
		// now display player scores
		final TotalScore totalprof = new TotalScore( );
		final TotalScore totalttl = new TotalScore( );
		for( Player p : sortOnPortfolio( game.getPlayers( ) ) ) {
			// name and portfolio
			final String pl = p.toString( ) + (p.getPortfolio( ) != null ? " (" + p.getPortfolio( ).toString( ) + ")" : "");
			txt += pad( pl, colsize, true ) + "|";
			
			// check if game data is available
			if( data == null ) {
				txt += "\n";
				continue;
			}
			
			// the player profit
			final TotalScore prof = data.getProfits( p );
			txt += pad( f( prof.getBestCase( ) ) + " (- " + f( prof.getBestCase( ) - prof.getWorstCase( ) ) + ") ", colsize, false ) + "|";
			totalprof.add( prof.getBestCase( ), prof.getBestCase( ) - prof.getWorstCase( ) );
			
			// the TTL
			final TotalScore ttl = data.getTTL( p, false );
			txt += pad( f( ttl.getBestCase( ) ) + " (" + f( -(ttl.getBestCase( ) - ttl.getWorstCase( )) ) + ") ", colsize, false ) + "|";
			totalttl.add( ttl.getBestCase( ), ttl.getBestCase( ) - ttl.getWorstCase( ) );
			
			// end of player scores
			txt += "\n";
		}
		
		// add totals
		txt += pad( "", (colsize + 1) * 3, true, '-' ) + "\n";
		txt += pad( "Totals: ", colsize, false ) + "|";
		txt += pad( f( totalprof.getBestCase( ) ) + " (- " + f( totalprof.getWorstCase( ) ) + ") ", colsize, false ) + "|";
		txt += pad( f( totalttl.getBestCase( ) ) + " (- " + f( totalttl.getWorstCase( ) ) + ") ", colsize, false ) + "\n";
		
		// and expected totals
		txt += "\n";
		txt += "Expected profits : " + pad( f( totalprof.getBestCase( ) - totalprof.getWorstCase( ) * .333 ), 10, false ) + "\n";
		txt += "Expected ttl     : " + pad( f( totalttl.getBestCase( ) - totalttl.getWorstCase( ) * .333 ), 10, false ) + "\n";
		
		return txt;
	}
	
	/**
	 * Formats the number
	 * 
	 * @param num The number to format
	 * @return the formatted number
	 */
	private String f( double num ) {
		return (int)(num / 1000.0) + " K";
	}
	
	/**
	 * Pads the string to the left or right to get the correct amount of
	 * characters, uses spaces
	 * 
	 * @param s The string to pad
	 * @param size The padded string size
	 * @param padright True to pad on the right size, false for left
	 * @return The padded text 
	 * @throws IllegalArgumentException if the string is larger than the
	 * specified padded size
	 */
	private String pad( String s, int size, boolean padright ) throws IllegalArgumentException {
		return pad( s, size, padright, ' ' );
	}
	
	/**
	 * Pads the string to the left or right to get the correct amount of
	 * characters
	 * 
	 * @param s The string to pad
	 * @param size The padded string size
	 * @param padright True to pad on the right size, false for left
	 * @param padchar The character used to pad
	 * @return The padded text 
	 * @throws IllegalArgumentException if the string is larger than the
	 * specified padded size
	 */
	private String pad( String s, int size, boolean padright, char padchar ) throws IllegalArgumentException {
		if( s.length( ) > size ) throw new IllegalArgumentException( "String is too large" );
		
		String s2 = "" + s;
		while( s2.length( ) < size )
			if( padright ) s2 += padchar; else s2 = padchar + s2;
		
		return s2;
	}
	
	/**
	 * Sorts the player on portfolio name
	 * 
	 * @param players The set of players
	 * @return The sorted list of players
	 */
	protected List<Player> sortOnPortfolio( Collection<Player> players ) {
		// use insert sort
		final List<Player> sorted = new ArrayList<Player>( );
		for( Player p : players ) {
			for( int i = 0; i <= sorted.size( ); i++ ) {
				// insert as last element
				if( i == sorted.size( ) ) {
					sorted.add( p );
					break;
				}
					
				// insert here?
				if( p.getPortfolio( ).getDescription( ).compareToIgnoreCase( sorted.get( i ).getPortfolio( ).getDescription( ) ) < 0 ) {
					sorted.add( i, p );
					break;
				}
			}
		}
		
		return sorted;
	}
	
	/**
	 * Initialises the game tracer UI from the command line options
	 * @param args
	 */
	public static void main( String[] args ) {
		// initialise the UI
		final GameTraceUI ui = new GameTraceUI( );
		
		// check if a webroot is specified
		if( args.length > 0 )
			ui.setWebRoot( args[ 0 ] );
		
		// setup UI and run it
		try {
			// load trace file if specified
			if( args.length > 1 )
				ui.readTraceFile( args[ 1 ] );
			
			// and show the UI
			ui.setVisible( true );			
		} catch( Exception e ) {
			System.err.println( "Error while initialising tracer UI:" );
			e.printStackTrace( );
		}		
	}
}
