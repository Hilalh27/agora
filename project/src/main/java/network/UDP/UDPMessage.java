package network.UDP;

import java.net.InetAddress;

public record UDPMessage(String content, InetAddress origin) { }