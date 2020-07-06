/**
 * @file GameWidget.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         17 jul. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.scoretable;

import java.util.Comparator;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.gamedata.TotalScore;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.controls.DataTable;
import plangame.gwt.client.widgets.controls.DataTableColumn;
import plangame.gwt.client.widgets.listeners.TTLWidget;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Portfolio;

/**
 * Widget that can be used to display scores
 *
 * @author Joris Scharpff
 */
public class ScoreTableWidget extends BasicWidget implements JointPlanUpdateListener, TTLWidget {
	/** The data table */
	protected DataTable<Player> tblScores;
	
	/** The profits of each player */
	protected ObjectMap<Player, TotalScore> profits;
	
	/** The TTL score of each player */
	protected ObjectMap<Player, TotalScore> ttlscores;
	
	/** Update data on every change or only on key moments */
	protected boolean updateLive;
	
	/** Sort table based on score rank */
	protected boolean sortOnRank;
	
	/** Display TTL info relative */
	protected boolean relative;
	
	/** The data table mode */
	protected ScoreTableMode mode;
	
	/** Available score table modes */
	public enum ScoreTableMode {
		/** Profits only */
		Profits,
		/** TTL only */
		TTL,
		/** Both scores */
		Both
	}
	
	/** Display the worst case also? */
	protected boolean showWorstCase;
	
	/**
	 * Creates a new game widget
	 * 
	 * @param mode The score table display mode
	 */
	public ScoreTableWidget( ScoreTableMode mode ) {
		super( );
		
		// create empty score maps
		profits = new ObjectMap<Player, TotalScore>( );
		ttlscores = new ObjectMap<Player, TotalScore>( );
		
		// set the default worst case display mode
		setShowWorstCase( mode == ScoreTableMode.Both );
		
		// init the widget
		initTable( mode );
		initWidget( tblScores );

		// set sorting
		setSortOnRank( false );
		
		// set update live to default
		setUpdateLive( false );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight( String height ) {
		super.setHeight( height );
		
		tblScores.setHeight( height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth( String width ) {
		super.setWidth( width );
		
		tblScores.setWidth( width );
	}

	/**
	 * Shows or hides the worst case in the table
	 * 
	 * @param worstcase True to enable
	 */
	public void setShowWorstCase( boolean worstcase ) {
		this.showWorstCase = worstcase;
		
		if( isUpdateLive( ) )
			getData( ).refresh( );
	}
	
	/**
	 * @return True if the worst case should be displayed
	 */
	public boolean showWorstCase( ) {
		return showWorstCase;
	}
	
	/**
	 * True to sort scores on rank (not available in mode Both)
	 * 
	 * @param sort True to sort
	 */
	public void setSortOnRank( boolean sort ) {
		if( mode == ScoreTableMode.Both ) return;
		
		this.sortOnRank = sort;
		
		// FIXME remove existing sort is not possible
		if( mode == ScoreTableMode.Profits ) {
			tblScores.addSortHandler( 3, new Comparator<Player>( ) {
				
				@Override
				public int compare( Player o1, Player o2 ) {
					final int r1 = getGameData( ).getData( ).getProfitRank( o1 );
					final int r2 = getGameData( ).getData( ).getProfitRank( o2 );
					
					return r2 - r1;
				}
			} );
		} else {
			tblScores.addSortHandler( 3, new Comparator<Player>( ) {
				
				@Override
				public int compare( Player o1, Player o2 ) {
					final int r1 = getGameData( ).getData( ).getTTLRank( o1 );
					final int r2 = getGameData( ).getData( ).getTTLRank( o2 );
					
					return r2 - r1;
				}
			} );
		}
	}
	
	/**
	 * @see plangame.gwt.client.widgets.listeners.TTLWidget#setDisplayRelative(boolean)
	 */
	@Override
	public void setDisplayRelative( boolean relative ) {
		this.relative = relative;
		
		tblScores.refresh( );
	}
	
	/**
	 * @return True if TTL is displayed relative
	 */
	public boolean isRelative( ) {
		return relative;
	}
	
	/**
	 * Initialises provided widgets
	 * 
	 * @param mode The table mode
	 */
	private void initTable( final ScoreTableMode mode ) {
		this.mode = mode;

		// setup the data table
		tblScores = new DataTable<Player>( ) {
			@Override public void initColumns( ) {
				// Name
				addColumn( Lang.text.W_SCMGR_SB_ColPlayerName( )	, "20%", new DataTableColumn<Player>( ) {				
					@Override public String render( Player key ) {
						return key.getDescription( );
					}
				} );

				// Portfolio
				addColumn( Lang.text.W_SCMGR_SB_ColPortfolio( )	, "14%", new DataTableColumn<Player>( ) {				
					@Override public String render( Player key ) {
						final Portfolio pf = key.getPortfolio( );
						return (pf == null ? Lang.text.Unassigned( ) : pf.getDescription( ));
					}
				} );
				
				// its profits (best case and delta)
				if( mode == ScoreTableMode.Profits || mode == ScoreTableMode.Both ) {
					addColumn( Lang.text.W_SCMGR_SB_ColProfits( ), "25%", new DataTableColumn<Player>( ) {
						@Override public String render( Player key ) {
							if( !gameDataReady( ) ) return "";
							
							final TotalScore profit = getGameData( ).getData( ).getProfits( key );
							
							return Format.f( profit.getBestCase( ), Style.CurrK ) + (showWorstCase( ) ? " (- " + Format.f( Math.abs( profit.getDelta( ) ), Style.CurrK ) + ")" : "");
						}
					} );
				
					// profit rank
					addColumn( Lang.text.W_SCMGR_SB_ColProfitRank( ), "8%", new DataTableColumn<Player>( ) {
						@Override public String render( Player key ) {
							if( !gameDataReady( ) ) return "";
							
							return Format.f( getGameData( ).getData( ).getProfitRank( key ), Style.Rank );
						}
					} );
					
				}
				
				// and its TTL score
				if( mode == ScoreTableMode.TTL || mode == ScoreTableMode.Both ) {
					addColumn( Lang.text.W_SCMGR_SB_ColTTL( ), "25%", new DataTableColumn<Player>( ) {
						@Override public String render( Player key ) {
							if( !gameDataReady( ) ) return "";
	
							final TotalScore ttl = getGameData( ).getData( ).getTTL( key, isRelative( ) );
							final Style style = (isRelative( ) ? Style.Percentage2 : Style.IntK);
							
							return Format.f( ttl.getBestCase( ), style ) + (showWorstCase( ) ? " (+ " + Format.f( Math.abs( ttl.getDelta( ) ), style ) + ")" : "");
						}
					} );
	
					// TTL rank
					addColumn( Lang.text.W_SCMGR_SB_ColTTLRank( ), "8%", new DataTableColumn<Player>( ) {
						@Override public String render( Player key ) {
							if( !gameDataReady( ) ) return "";
							
							return Format.f( getGameData( ).getData( ).getTTLRank( key ), Style.Rank );
						}
					} );
					
				}
				
			}
		};
		tblScores.setEmptyText( Lang.text.W_PMGR_NoPlayers( ) );
	}
	
	/**
	 * @return The data table
	 */
	public DataTable<Player> getData( ) {
		return tblScores;
	}
	
	/**
	 * Set to true to update score on every plan change, false will only update
	 * on key moments
	 * 
	 * @param update True to enable live updates
	 */
	public void setUpdateLive( boolean update ) {
		this.updateLive = update;
	}
	
	/**
	 * @return True if the scores are updated on every plan change
	 */
	public boolean isUpdateLive( ) {
		return updateLive;
	}

	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		getData( ).clear( );
		// make sure all clients are on the score board when the game starts
		for( Player p : jplan.getPlayers( ) )
			getData( ).setItem( p );
		
		getData( ).refresh( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// only update data 
		if( isUpdateLive( ) )
			getData( ).refresh( );
	}
}
