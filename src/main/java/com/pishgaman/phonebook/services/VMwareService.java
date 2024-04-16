package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.utils.NetworkUtils;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pishgaman.phonebook.dtos.VirtualMachineDto;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VMwareService {

    @Value("${vmware.vsphere.url}")
    private String serverUrl;

    @Value("${vmware.vsphere.username}")
    private String username;

    @Value("${vmware.vsphere.password}")
    private String password;

    public List<VirtualMachineDto> retrieveVmDetails() throws Exception {
        ServiceInstance si = new ServiceInstance(new URL(serverUrl), username, password, true);
        Folder rootFolder = si.getRootFolder();
        ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");

        List<VirtualMachineDto> vmList = new ArrayList<>();
        if (vms != null) {
            for (ManagedEntity entity : vms) {
                VirtualMachine vm = (VirtualMachine) entity;
                VirtualMachineConfigInfo vmConfig = vm.getConfig();
                String vmName = vm.getName();
                String ipAddress = vm.getGuest().getIpAddress();
                String guestOsFullName = vm.getGuest().getGuestFullName();
                String guestHostName = NetworkUtils.extractHostname(vm.getGuest().getHostName());
                String notes = vmConfig.getAnnotation();

                if (notes != null && !notes.isEmpty()) {
                    vmList.add(new VirtualMachineDto(vmName, ipAddress, guestOsFullName, guestHostName, notes));
                }
            }
        }

        si.getServerConnection().logout();
        return vmList;
    }
}
