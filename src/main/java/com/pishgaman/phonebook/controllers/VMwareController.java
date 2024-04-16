package com.pishgaman.phonebook.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pishgaman.phonebook.services.VMwareService;
import com.pishgaman.phonebook.dtos.VirtualMachineDto;

import java.util.List;

@CrossOrigin
@RestController
public class VMwareController {
    private final VMwareService vmwareService;
    @Autowired
    public VMwareController(VMwareService vmwareService) {
        this.vmwareService = vmwareService;
    }

    @GetMapping("/api/vmware/vms")
    public ResponseEntity<?> getVmDetails() {
        try {
            List<VirtualMachineDto> vms = vmwareService.retrieveVmDetails();
            if (vms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(vms);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
