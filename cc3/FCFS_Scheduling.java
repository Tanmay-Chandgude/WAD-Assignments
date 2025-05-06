import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.*;

public class FCFS_Scheduling {
    public static void main(String[] args) {
        int numUsers = 1;
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false;

        CloudSim.init(numUsers, calendar, traceFlag);
        Datacenter datacenter0 = createDatacenter("Datacenter_0");
        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();

        List<Vm> vmList = new ArrayList<>();
        int mips = 1000;
        int vmCount = 4;

        for (int i = 0; i < vmCount; i++) {
            Vm vm = new Vm(i, brokerId, mips, 1, 1024, 10000, 10000, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        broker.submitVmList(vmList);

        List<Cloudlet> cloudletList = new ArrayList<>();
        int cloudletCount = 8;
        long length = 40000;
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudletCount; i++) {
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        broker.submitCloudletList(cloudletList);

        assignCloudletsToVMs(broker, cloudletList, vmList);
        CloudSim.startSimulation();
        List<Cloudlet> newList = broker.getCloudletReceivedList();
        CloudSim.stopSimulation();

        printCloudletResults(newList);
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 10000;
        int ram = 16384;
        long storage = 1000000;
        int bw = 10000;
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));
        hostList.add(new Host(0, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList)));
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, 10, 3, 0.05, 0.1, 0.1);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static void assignCloudletsToVMs(DatacenterBroker broker, List<Cloudlet> cloudletList, List<Vm> vmList) {
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmList.get(i % vmList.size()); // Assigning in FCFS manner
            cloudlet.setVmId(vm.getId());
        }
    }

    private static void printCloudletResults(List<Cloudlet> list) {
        System.out.println("\nRESULTS\nCloudlet ID | Status | VM ID | Time | Start Time | Finish Time");
        for (Cloudlet cloudlet : list) {
            System.out.printf("%10d | %6s | %5d | %4.2f | %10.2f | %11.2f\n",
                    cloudlet.getCloudletId(),
                    cloudlet.getStatus() == Cloudlet.SUCCESS ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    cloudlet.getActualCPUTime(),
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime());
        }
    }
}