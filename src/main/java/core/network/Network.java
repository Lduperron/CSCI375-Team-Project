package core.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import core.shared.Alert;
import core.shared.Background;
import core.shared.Character;
import core.shared.Decision;
import core.shared.Dialogue;
import core.shared.Document;
import core.shared.Face;
import core.shared.Level;
import core.shared.Message;
import core.shared.PersonPicture;
import core.shared.Posture;
import core.shared.Query;
import core.shared.Result;
import core.shared.Role;
import core.shared.SoundTrack;

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
		/*
		 * Register the message we'll be passing between the client(s) and
		 * server
		 */
		kryo.register(NetMessage.class);
		/*
		 * Register all classes in core.shared
		 */
		kryo.register(Alert.class);
		kryo.register(Background.class);
		kryo.register(Character.class);
		kryo.register(Decision.class);
		kryo.register(Dialogue.class);
		kryo.register(Document.class);
		kryo.register(Document.Type.class);
		kryo.register(Face.class);
		kryo.register(Level.class);
		kryo.register(Message.class);
		kryo.register(PersonPicture.class);
		kryo.register(Posture.class);
		kryo.register(Result.class);
		kryo.register(Role.class);
		kryo.register(Query.class);
		kryo.register(SoundTrack.class);

		kryo.register(List.class);
		kryo.register(LinkedList.class);

		kryo.register(HashMap.class);

		kryo.register(String.class);
		kryo.register(String[].class);
	}
}
