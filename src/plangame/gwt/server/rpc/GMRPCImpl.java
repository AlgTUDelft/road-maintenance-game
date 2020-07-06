/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package plangame.gwt.server.rpc;

import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.gwt.client.gamemanager.GMRPC;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidGameStateException;
import plangame.gwt.shared.exceptions.InvalidPlanException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.model.object.BasicID;
import plangame.model.tasks.Portfolio;

/**
 * Server side implementation of GameManager RPC
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GMRPCImpl extends GameClientImpl implements GMRPC {
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#getPortfolios(plangame.model.object.BasicID)
	 */
	@Override
	public List<Portfolio> getPortfolios( BasicID gmID ) throws NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException {
		return getGameServer( getClient( gmID ) ).getPortfolios( );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#assignPortfolio(plangame.model.object.BasicID, plangame.model.object.BasicID)
	 */
	@Override
	public void assignPortfolio( BasicID clientID, BasicID pfID ) throws GameServerException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException {
		getGameServer( getClient( clientID ) ).assignPortfolio( (SPClient)getClient( clientID ), pfID );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#setGameState(plangame.model.object.BasicID, plangame.gwt.shared.enums.GameState)
	 */
	@Override
	public GameState setGameState( BasicID clientID, GameState state ) throws NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException {
		return getGameServer( getClient( clientID ) ).forceGameState( state );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#setClientState(plangame.model.object.BasicID, plangame.gwt.shared.state.SPState.PlayerStatus)
	 */
	@Override
	public ClientState setClientState( BasicID clientID, ClientState state ) throws GameServerException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException {
		final SPClient client = (SPClient) getClient( clientID );
		return getGameServer( client ).forceClientState( client, state );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#startGame(java.lang.String)
	 */
	@Override
	public void startGame( BasicID clientID ) throws NoServerException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, GameServerException {
		getGameServer( getClient( clientID ) ).startGame( );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#startPlanRound(boolean)
	 */
	@Override
	public void startPlanRound( BasicID clientID ) throws NoServerException, InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException {
		getGameServer( getClient( clientID ) ).initPlanRound( );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#startExecute(plangame.model.object.BasicID, plangame.gwt.shared.ExecutionInfo)
	 */
	@Override
	public void startExecute( BasicID gmID, ExecutionInfo execinfo ) throws InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException, InvalidPlanException {
		getGameServer( getClient( gmID ) ).initExecution( execinfo );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#stopExecute(plangame.model.object.BasicID)
	 */
	@Override
	public void stopExecute( BasicID gmID ) throws InvalidGameStateException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException, NoServerException {
		getGameServer( getClient( gmID ) ).stopExecution( );
	}
	
	/**
	 * @see plangame.gwt.client.gamemanager.GMRPC#submitPending(java.lang.String, plangame.game.plans.PlanStepResult, java.lang.boolean)
	 */
	@Override
	public void submitPending( BasicID clientID, PlanStepResult results, boolean stop ) throws GameServerException, NoServerException, InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, InvalidPlanException {
		getGameServer( getClient( clientID ) ).handlePending( results, stop );
	}
}
