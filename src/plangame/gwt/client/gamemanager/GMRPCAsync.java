package plangame.gwt.client.gamemanager;

import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.gwt.client.rpc.GameClientRPCAsync;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.model.object.BasicID;
import plangame.model.tasks.Portfolio;

import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The async counterpart of <code>GMRPC</code>.
 */
@SuppressWarnings("javadoc")
public interface GMRPCAsync extends GameClientRPCAsync {
	public void getPortfolios( BasicID gmID, AsyncCallback<List<Portfolio>> callback );
	public void assignPortfolio( BasicID clientID, BasicID pfID, AsyncCallback<Void> callback );
	public void setGameState( BasicID gmID, GameState state, AsyncCallback<GameState> callback );
	public void setClientState( BasicID clientID, ClientState state, AsyncCallback<ClientState> callback );
	public void startGame( BasicID gmID, AsyncCallback<Void> callback );
	public void startPlanRound( BasicID gmID, AsyncCallback<Void> callback );
	public void startExecute( BasicID gmID, ExecutionInfo execinfo, AsyncCallback<Void> callback );
	public void stopExecute( BasicID gmID, AsyncCallback<Void> callback );
	public void submitPending( BasicID gmID, PlanStepResult results, boolean stop, AsyncCallback<Void> callback );
}
