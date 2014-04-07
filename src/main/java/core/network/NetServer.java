package core.network;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.server.ServerEngine;
import core.shared.Message;
import core.shared.Position;

/**
 * ServerEngine end for KryoNet network communications
 * 
 * @see https://code.google.com/p/kryonet/
 * 
 * @author marius
 */
public class NetServer extends Network {
	private ServerEngine gameServer;
	private Server server;

	/**
	 * Constructor for NetServer
	 * 
	 * @param gameServer
	 *            Reference to a {@link ServerEngine}
	 * @throws IOException
	 */
	public NetServer(final ServerEngine incomingGameServer) throws IOException {
		this.gameServer = incomingGameServer;
		server = new Server(BUFFER_SIZE, BUFFER_SIZE);
		/* start a thread to handle incoming connections */
		server.start();

		try {
			server.bind(tcpPort);
		} catch (IOException e) {
			throw new IOException("Unable to bind to port");
		}

		/**
		 * For consistency, the classes to be sent over the network are
		 * registered by the same method for both the client and server
		 * inherited from SuperClass Network.
		 */
		register(server);

		/**
		 * Add a Listener to handle receiving objects
		 * 
		 * Typically a Listener has a series of instanceof checks to decide what
		 * to do with the object received.
		 * 
		 * Note the Listener class has other notification methods that can be
		 * overridden.
		 */
		server.addListener(new Listener() {
			/**
			 * Override received method of Listener to specify game specific
			 * management of received objects
			 */
			public void received(Connection connection, Object object) {

				if (object instanceof NetMessage) {
					NetMessage netMsg = (NetMessage) object;

					/**
					 * Process message received from client
					 */
					switch (netMsg.msg) {
					case TEST:
						System.out.println("Received Message from Client");
						gameServer.test();
						
						break;
					case SPAWN:
						gameServer.spawnMob();
						
						break;
						
					case REQUESTSTATE:
						gameServer.sendCompleteState();
						break;
						
					case REQUESTMOVE: // TODO: ... to most of these, send the connection in particular that asked for it.
						Position P = (Position) netMsg.obj;
						gameServer.requestMove(P);
						break;
						
					case MOUSEEVENTTOSERVER:
						int MouseEventUID = (int) netMsg.obj;
						gameServer.mouseEvent(MouseEventUID);
						break;
						

						
						
						
					default:
						// invalid messages are simply ignored
						break;
					}
				}
			}

			/**
			 * Called when the remote end is no longer connected. Used to trap
			 * accidental disconnects, cause client won't be able to tell us
			 * that it disconnected
			 */
			public void disconnected(Connection connection) {

			}
		}); // end of addListener
	} // end of constructor


	/**
	 * Stops the ServerEngine
	 */
	public void killServer() {

		server.stop();
	}

	/**
	 * Send an Enumerated {@link Message} to all {@link Client}s
	 * 
	 * @param msg
	 */
	public void sendAll(Message msg) {
		sendAll(msg, null);
	}

	/**
	 * Send an Enumerated {@link Message} and a registered (
	 * {@link Kryo#register(Class)}) Object to all {@link Client}s
	 * 
	 * @param msg
	 * @param obj
	 */
	public void sendAll(Message msg, Object obj) {
		NetMessage netMsg = new NetMessage(msg, obj);
		server.sendToAllTCP(netMsg);
	}

	/**
	 * Send an Enumerated {@link Message} to a specific {@link Connection}
	 * 
	 * @param conn
	 * @param msg
	 */
	public void sendClient(Connection conn, Message msg) {
		server.sendToTCP(conn.getID(), msg);
	}



	/**
	 * Send an Enumerated {@link Message} and a registered (
	 * {@link Kryo#register(Class)}) Object to the client with a specific
	 * {@link Role}
	 * 
	 * @param role
	 * @param msg
	 * @param obj
	 */

	public static void main(String [ ] args) {
		 Server server = new Server();
		    server.start();
		    try {
				server.bind(54555, 54777);
				System.out.println("Server Started");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}