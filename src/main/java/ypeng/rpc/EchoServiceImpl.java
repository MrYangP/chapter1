package ypeng.rpc;

import ypeng.EchoService;

public class EchoServiceImpl implements EchoService {

	public EchoServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public void echo(String string) {
		System.out.println(string == null ? "I'm OK!" : "--->> I'm OK");
	}

}
