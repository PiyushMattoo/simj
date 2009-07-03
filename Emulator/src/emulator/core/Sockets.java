package emulator.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

public class Sockets {
	private static Logger logger = Logger.getLogger(Sockets.class
			.getCanonicalName());

	ServerSocket sim_create_sock() throws IOException {
		ServerSocket newsock;
		newsock = new ServerSocket();
		return newsock;
	}

	ServerSocket sim_master_sock(int port) throws IOException {

		ServerSocket newsock;

		newsock = sim_create_sock(); /* create socket */
		if (newsock == null) /* socket error? */
			return newsock;

		InetSocketAddress sa = new InetSocketAddress(port);
		newsock.bind(sa);

		return newsock; /* got it! */
	}

	// TODO Make this non-block
	Socket sim_connect_sock(InetAddress ip, int port) throws IOException {
		Socket newsock;

		newsock = new Socket(); /* create socket */
		if (newsock == null) /* socket error? */
			return newsock;

		InetSocketAddress sa = new InetSocketAddress(ip, port);

		// sim_setnonblock (newsock); /* set nonblocking */

		newsock.connect(sa, port);

		return newsock; /* got it! */
	}

	// TODO Make this non-block
	Socket sim_accept_conn(ServerSocket master) throws IOException {

		Socket newsock = master.accept();

		// sta = sim_setnonblock (newsock); /* set nonblocking */

		return newsock;
	}

	boolean sim_check_conn(Socket sock) {

		return sock.isConnected();

	}

	// TODO Make this non-blocking
	long sim_read_sock(Socket sock, ByteBuffer buf) throws IOException {

		return sock.getChannel().read(buf);

	}

	long sim_write_sock(Socket sock, ByteBuffer msg) throws IOException {
		return sock.getChannel().write(msg);

	}

	void sim_close_sock(Socket sock) throws IOException {
		sock.close();
		return;
	}

	void sim_setnonblock(ServerSocket sock) throws IOException {

		ServerSocketChannel sschnl = sock.getChannel();
		sschnl.configureBlocking(false);

	}

}
