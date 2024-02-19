package io.github.hero821.example.jmdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.net.InetAddress;

/**
 * @author li.qiang
 */
public class Application {
    public static void main(String[] args) throws Exception {
        // Create a JmDNS instance
        JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

         String type = "_http._tcp.local.";
//        String type = "_osware._tcp.local.";
        // Add a service listener
        jmdns.addServiceListener(type, new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                System.out.println("Service added: " + event.getInfo());
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                System.out.println("Service removed: " + event.getInfo());
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                System.out.println("Service resolved: " + event.getInfo());
            }
        });

        // Wait a bit
        Thread.sleep(30000);
    }
}
