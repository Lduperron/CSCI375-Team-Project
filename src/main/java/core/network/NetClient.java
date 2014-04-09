package core.network;

import gameCode.obj.Obj;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

import core.client.ClientEngine;

import core.shared.DistilledObject;
import core.shared.Message;
import core.shared.Position;

/**
 * Client end for KryoNet network communications
 * 
 * @see https://code.google.com/p/kryonet/
 * 
 * Implements Client specific functionality for {@link Network} - overrides
 * {@link Listener} to implement calls to the {@link ClientEngine} API
 * 
 * @author marius
 */
public class NetClient extends Network {
	private final ClientEngine gameClient;
	private final Client client;
	private static final int timeout = 5000;

	/**
	 * Constructor for NetClient
	 * 
	 * @param gameClient
	 *            Reference to a {@link ClientEngine}
	 * @param host
	 *            Name or ip address of the Server to connect to
	 * @throws IOException
	 */
	public NetClient(final ClientEngine incomingGameClient, String host)
			throws IOException {
		this.gameClient = incomingGameClient;
		client = new Client(BUFFER_SIZE, BUFFER_SIZE);
		client.start();

		/**
		 * For consistency, the classes to be sent over the network are
		 * registered by the same method for both the client and server
		 * inherited from SuperClass Network.
		 */
		register(client);

		try {
			client.connect(timeout, host, tcpPort);
		} catch (IOException e) {
			throw new IOException("Unable to connect to Server");
		}
		
		this.send(Message.CONNECT);
		/**
		 * change timeout to 60 secs so that client will not accidently
		 * disconnect
		 */
		setTimeout(60000);

		/**
		 *  ThreadedListener runs the listener methods on a different thread.
		 */
		client.addListener(new ThreadedListener(new Listener() {

			/**
			 * Override received method of Listener to specify game specific
			 * management of received objects
			 */
			Position newPos ;
			
			public void received(Connection connection, Object object) {
				if (object instanceof NetMessage) {
					final NetMessage netMsg = (NetMessage) object;
					/**
					 *  Process the message received from the server
					 */
					switch (netMsg.msg) {

					case TEST:
						System.out.println("Message from Server Received");
						break;
						
					case NEWOBJECT:
						Obj o = (Obj) netMsg.obj;
						gameClient.addToWorld(o);
						break;
						
					case YOUCONTROL:
						int UID = (Integer) netMsg.obj;
						gameClient.assignControl(UID);
						break;
						
					case OBJMOVE:
						newPos = (Position) netMsg.obj;
						gameClient.queueObjectPosition(newPos);
						break;
						
					case OBJRELOCATE:
						newPos = (Position) netMsg.obj;
						gameClient.objectRelocate(newPos);
						break;
					
					case MOUSEEVENTFROMSERVER:
						int MouseEvent = (Integer) netMsg.obj;
						gameClient.mouseEvent(MouseEvent);
						break;
						
						
					default:
						// invalid messages are simply ignored
						break;
					}
				}
			}

			/**
			 * Called when the remote end is no longer connected. Used to trap
			 * accidental disconnects cause server won't be able to tell us that
			 * it disconnected
			 */
			public void disconnected(Connection connection) {

			}
		})); // end of addListener
	} // end of constructor

	/**
	 * Send an Enumerated {@link Message}
	 * 
	 * @param msg
	 */
	public void send(Message msg) {
		send(msg, null);
	}

	/**
	 * Send an Enumerated {@link Message} and a registered (
	 * {@link Kryo#register(Class)}) Object
	 * 
	 * @param msg
	 * @param obj
	 */
	public void send(Message msg, Object obj) {
		NetMessage netMsg = new NetMessage(msg, obj);
		client.sendTCP(netMsg);
	}

	/**
	 * Sets {@line Connection} timeout to a value between 0 and 60 seconds
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		if ((timeout > 0) && (timeout < 60001))
			client.setTimeout(timeout);
	}

	/**
	 * Disconnects the KryoNet Client
	 */
	public void kill() {
		client.stop();
	}
	
	public static void main(String [ ] args) throws IOException {
		NetClient client = new NetClient(new ClientEngine(), "127.0.0.1");
	    client.send(Message.TEST);		
	}
}
