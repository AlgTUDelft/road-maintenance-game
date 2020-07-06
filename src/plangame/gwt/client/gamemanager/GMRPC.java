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
package plangame.gwt.client.gamemanager;

import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.gwt.client.rpc.GameClientRPC;
import plangame.gwt.shared.ExecutionInfo;
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

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * All game manager specific RPC commands
 *
 * @author Joris Scharpff
 */
@RemoteServiceRelativePath ("GMRPC" )
public interface GMRPC extends GameClientRPC {
	/**
	 * Retrieves the list of available portfolios of this game
	 * 
	 * @param gmID The ID of the GM
	 * @return The list of portfolios
	 * @throws NoSuchGameServerException
	 * @throws ClientNotConnectedException
	 * @throws ClientNotInGameException
	 * @throws NoServerException
	 */
	public List<Portfolio> getPortfolios( BasicID gmID ) throws NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException;
	
	/**
	 * Assigns the portfolio to the client
	 * 
	 * @param clientID The client to assign
	 * @param pfID The ID of the portfolio
	 * @throws GameServerException if the portfolio is already assigned or the
	 * client was already assigned a portfolio or the client is not a SP
	 * @throws NoSuchGameServerException
	 * @throws ClientNotConnectedException
	 * @throws ClientNotInGameException
	 * @throws NoServerException
	 */
	public void assignPortfolio( BasicID clientID, BasicID pfID ) throws GameServerException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException;
	
	/**
	 * Sets the current game state
	 * 
	 * @param clientID The ID of the GM client
	 * @param state the new game state
	 * @return The previous game state
	 * @throws NoSuchGameServerException
	 * @throws ClientNotConnectedException
	 * @throws ClientNotInGameException
	 * @throws NoServerException
	 */
	public GameState setGameState( BasicID clientID, GameState state ) throws NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException;
	
	/**
	 * Sets the status of a client
	 * 
	 * @param clientID The ID of the client to set status of
	 * @param state The new client status
	 * @return The previous client status
	 * @throws GameServerException if the client state cannot be set in the
	 * current state of the game
	 * @throws NoSuchGameServerException
	 * @throws ClientNotConnectedException
	 * @throws ClientNotInGameException
	 * @throws NoServerException
	 */
	public ClientState setClientState( BasicID clientID, ClientState state ) throws GameServerException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException;
	
	/**
	 * Starts the game, players cannot joint the game afterwards
	 * 
	 * @param gmID The ID of the GM that wants to start the game
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoServerException 
	 * @throws GameServerException if either no players are connected or any of
	 * the players has no portfolio assigned
	 */
	public void startGame( BasicID gmID ) throws NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException, GameServerException;
	
	/**
	 * Initialises a new planning round
	 * 
	 * @param gmID The ID of the GM that wants to start the game
	 * @return The number of clients that have been notified
	 * @throws InvalidGameStateException if we cannot plan in the current state
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoSuchGameServerException 
	 * @throws NoServerException 
	 */
	public void startPlanRound( BasicID gmID ) throws InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException;

	/**
	 * Executes the current joint plan
	 * 
	 * @param gmID The ID of the GM that issues the execute
	 * @param execinfo The execution info
	 * @throws InvalidGameStateException if the command is invalid in the current
	 *         state
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoSuchGameServerException 
	 * @throws NoServerException 
	 * @throws InvalidPlanException if the joint plan is not executable
	 */
	public void startExecute( BasicID gmID, ExecutionInfo execinfo ) throws InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException, InvalidPlanException;

	/**
	 * Ends the current execution, this will be in effect after the current round
	 * has been executed.
	 * 
	 * @param gmID The ID of the GM that issues the stop
	 * @throws InvalidGameStateException if the command is invalid in the current
	 *         state
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoSuchGameServerException 
	 * @throws NoServerException 
	 */
	public void stopExecute( BasicID gmID ) throws InvalidGameStateException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException, NoServerException;
	
	/**
	 * Ends the current round using the processed step results
	 * 
	 * @param gmID The ID of the GM that wants to start the game
	 * @param stop True to stop execution after handling
	 * @param results The processed step results
	 * @throws GameServerException if the submitted results still contains
	 * pending methods
	 * @throws InvalidGameStateException if the command is invalid in the current
	 *         state
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoSuchGameServerException 
	 * @throws NoServerException 
	 * @throws InvalidPlanException if the joint plan is not valid to execute
	 */
	public void submitPending( BasicID gmID, PlanStepResult results, boolean stop ) throws GameServerException, InvalidGameStateException, NoSuchGameServerException, ClientNotConnectedException, ClientNotInGameException, NoServerException, InvalidPlanException;
}
