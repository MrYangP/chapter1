package ypeng.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 服务发布者，监听客户端的TCP连接，连接后封装成Task，由线程池执行； 将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果；
 * 将执行结果对象反序列化，通过Socket发送给客户端； 远程服务调用完成之后，释放Socket等连接资源，防止句柄泄漏；
 * 
 * @author 15161
 *
 */
public class RpcExporter {

	static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public RpcExporter() {
		// TODO Auto-generated constructor stub
	}

	public static void exporter(String host, int port) throws IOException {
		ServerSocket server = new ServerSocket();
		server.bind(new InetSocketAddress(host, port));
		try {
			while (true) {
				executor.execute(new ExporterTask(server.accept()));
			}
		} finally {
			server.close();
		}
	}

	private static class ExporterTask implements Runnable {
		Socket client = null;

		public ExporterTask(Socket client) {
			this.client = client;
		}

		public void run() {
			ObjectInputStream input = null;
			ObjectOutputStream output = null;
			try {
				input = new ObjectInputStream(client.getInputStream());
				String interfaceName = input.readUTF();
				Class<?> service = Class.forName(interfaceName);
				String methodName = input.readUTF();
				Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
				Object[] arguments = (Object[]) input.readObject();
				Method method = service.getMethod(methodName, parameterTypes);
				output = new ObjectOutputStream(client.getOutputStream());
				output.writeObject(method.invoke(service.newInstance(), arguments));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (client != null) {
					try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

}
