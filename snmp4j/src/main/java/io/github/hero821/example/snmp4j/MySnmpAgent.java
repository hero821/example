package io.github.hero821.example.snmp4j;

import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.io.ImportModes;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.log.ConsoleLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * @author li.qiang
 */
@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "BusyWait"})
public class MySnmpAgent extends BaseAgent {
    static {
        LogFactory.setLogFactory(new ConsoleLogFactory());
        ConsoleLogFactory.getLogFactory().getRootLogger().setLogLevel(LogLevel.DEBUG);
    }

    protected MySnmpAgent() {
        super(new File("MySnmpAgent.BootCounterFile.cfg"), new File("MySnmpAgent.ConfigFile.cfg"), new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
    }

    @Override
    protected void registerManagedObjects() {
        // https://support.f5.com/csp/article/K14114
        try {
            // memTotalReal
            server.register(new MOScalar<Integer32>(new OID("1.3.6.1.4.1.2021.4.5.0"), MOAccessImpl.ACCESS_READ_ONLY, new Integer32()) {
                @Override
                public Integer32 getValue() {
                    return new Integer32(new Random().nextInt(10));
                }
            }, getDefaultContext());
        } catch (DuplicateRegistrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unregisterManagedObjects() {

    }

    @Override
    protected void addUsmUser(USM usm) {

    }

    @Override
    protected void addNotificationTargets(SnmpTargetMIB snmpTargetMIB, SnmpNotificationMIB snmpNotificationMIB) {

    }

    @Override
    protected void addViews(VacmMIB vacmMIB) {
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1, new OctetString("cpublic"), new OctetString("v1v2group"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString("cpublic"), new OctetString("v1v2group"), StorageType.nonVolatile);
        vacmMIB.addAccess(new OctetString("v1v2group"), new OctetString("public"), SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"), new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
    }

    @Override
    protected void addCommunities(SnmpCommunityMIB snmpCommunityMIB) {
        Variable[] com2sec = new Variable[]{new OctetString("public"), new OctetString("cpublic"), getAgent().getContextEngineID(), new OctetString("public"), new OctetString(), new Integer32(StorageType.nonVolatile), new Integer32(RowStatus.active)};
        SnmpCommunityMIB.SnmpCommunityEntryRow row = snmpCommunityMIB.getSnmpCommunityEntry().createRow(new OctetString("public2public").toSubIndex(true), com2sec);
        snmpCommunityMIB.getSnmpCommunityEntry().addRow(row);
    }

    @Override
    protected void initTransportMappings() throws IOException {
        transportMappings = new DefaultUdpTransportMapping[]{new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/161"))};
    }

    public void unregisterManagedObject(MOGroup moGroup) {
        moGroup.unregisterMOs(server, getContext(moGroup));
    }

    public static void main(String[] args) throws Exception {
        MySnmpAgent agent = new MySnmpAgent();
        agent.init();
        agent.loadConfig(ImportModes.REPLACE_CREATE);
        agent.addShutdownHook();
        agent.getServer().addContext(new OctetString("public"));
        agent.finishInit();
        agent.run();
        agent.sendColdStartNotification();

        agent.unregisterSnmpMIBs();
        agent.unregisterManagedObject(agent.getSnmpMpdMib());

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
