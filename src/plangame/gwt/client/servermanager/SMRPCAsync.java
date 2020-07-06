package plangame.gwt.client.servermanager;

import java.util.List;

import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.object.BasicID;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GMRPC</code>.
 */
@SuppressWarnings("javadoc")
public interface SMRPCAsync extends ClientRPCAsync {
	public void createGameServer( GameServerInfo serverInfo, AsyncCallback<GameServerInfo> callback );
	public void restartServer( BasicID gameID, AsyncCallback<GameServerInfo> callback );
	public void endServer( BasicID gameID, AsyncCallback<Integer> callback );
	public void getClientList( AsyncCallback<List<Client>> callback );
	public void reassign( BasicID clientID, BasicID targetID, AsyncCallback<Void> callback );
	public void kick( BasicID clientID, AsyncCallback<Boolean> callback );
	public void disconnectAll( AsyncCallback<Integer> callback );
}
