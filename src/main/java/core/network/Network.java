package core.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


import core.shared.Message;


/**
 * This class is a convenient place to keep things common to both the client and
 * server
 * 
 * @author marius
 * 
 */
public class Network {
	public static final int tcpPort = 54555;
	public static final int udpPort = 54777;

	public static final int BUFFER_SIZE = 16384;

	public Network() {
		super();
	}

	/**
	 * Registers objects that will be sent over the network
	 * 
	 * @param endPoint
	 */
	static public void register(EndPoint endPoint) {

		/*
		 * Instantiate the serializer
		 */
		Kryo kryo = endPoint.getKryo();
		
		endPoint.getKryo().setRegistrationRequired(false);
		
		/*
		 * Register the message we'll be passing between the client(s) and
		 * server
		 */
		kryo.register(NetMessage.class);
		/*
		 * Register all classes in core.shared
		 */

		kryo.register(Message.class);

		

		kryo.register(List.class);
		kryo.register(LinkedList.class);

		kryo.register(HashMap.class);

		kryo.register(String.class);
		kryo.register(String[].class);
		
	}
}
