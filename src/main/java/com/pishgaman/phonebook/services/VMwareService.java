package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import com.pishgaman.phonebook.utils.NetworkUtils;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pishgaman.phonebook.dtos.VirtualMachineDto;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VMwareService {
    private final AppSettingsRepository appSettingsRepository;



    @Value("${vmware.vsphere.url}")
    private String serverUrl;

    @Value("${vmware.vsphere.username}")
    private String username;

    @Value("${vmware.vsphere.password}")
    private String password;


    private String dbServerUrl = serverUrl;
    private String dbUsername = username;
    private String dbPassword = password;

    public List<VirtualMachineDto> retrieveVmDetails() throws Exception {

        Optional<AppSettings> appSettings = appSettingsRepository.findById(1L);

        if (appSettings.isPresent()) {
            AppSettings settings = appSettings.get();
            if (settings.getVsphereUrl() != null && !settings.getVsphereUrl().isEmpty()){
                dbServerUrl = settings.getVsphereUrl();
            }
            if (settings.getVsphereUsername() != null && !settings.getVsphereUsername().isEmpty()){
                dbUsername = settings.getVsphereUsername();
            }
            if (settings.getVspherePassword() != null && !settings.getVspherePassword().isEmpty()){
                dbPassword = settings.getVspherePassword();
            }
        }

        ServiceInstance si = new ServiceInstance(new URL(dbServerUrl), dbUsername, dbPassword, true);
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
