package io.github.hero821.example.snmp4j;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.log.ConsoleLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.util.List;

/**
 * @author li.qiang
 */
public class MySnmpManager {
    static {
        LogFactory.setLogFactory(new ConsoleLogFactory());
        ConsoleLogFactory.getLogFactory().getRootLogger().setLogLevel(LogLevel.DEBUG);
    }

    public static void main(String[] args) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setAddress(GenericAddress.parse("udp:39.170.53.33/38036"));
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(3000);
        target.setRetries(3);

        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());

        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(".1.3.6.1.4.1.3375.2.2.10.1.2.1.1"));
        for (TreeEvent event : events) {
            VariableBinding[] varBindings = event.getVariableBindings();
            for (VariableBinding varBinding : varBindings) {
                System.out.println("." + varBinding.getOid().toString() + ":" + varBinding.getVariable().toString());
            }
        }
        events = treeUtils.getSubtree(target, new OID(".1.3.6.1.4.1.3375.2.2.10.2.3.1.10"));
        for (TreeEvent event : events) {
            VariableBinding[] varBindings = event.getVariableBindings();
            for (VariableBinding varBinding : varBindings) {
                System.out.println("." + varBinding.getOid().toString() + ":" + varBinding.getVariable().toString());
            }
        }
    }
}
