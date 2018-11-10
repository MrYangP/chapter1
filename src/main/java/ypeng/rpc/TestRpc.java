package ypeng.rpc;

import java.net.InetSocketAddress;

import ypeng.EchoService;

public class TestRpc {

	public static void main(String[] args) {
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					RpcExporter.exporter("localhost", 8080);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		RpcImporter<EchoService> importer = new RpcImporter<EchoService>();
		EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 8080));
		echo.echo("Are you ok ?");
	}

}
