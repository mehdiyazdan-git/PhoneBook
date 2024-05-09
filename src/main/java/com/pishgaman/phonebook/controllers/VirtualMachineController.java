package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.entities.VirtualMachine;
import com.pishgaman.phonebook.repositories.VirtualMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/virtual-machines")
public class VirtualMachineController {

    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    @GetMapping
    public List<VirtualMachine> getAllVirtualMachines() {
        return virtualMachineRepository.findAll();
    }
}

