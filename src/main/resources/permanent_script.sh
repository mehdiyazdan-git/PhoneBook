#!/bin/bash
echo 'export SERVER_PORT=8081' >> ~/.bashrc
echo 'export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/phonebook_db"' >> ~/.bashrc
echo 'export SPRING_DATASOURCE_USERNAME="postgres"' >> ~/.bashrc
echo 'export SPRING_DATASOURCE_PASSWORD="sgsec@1390"' >> ~/.bashrc
echo 'export VMWARE_VSPHERE_URL="https://192.168.3.2/sdk"' >> ~/.bashrc
echo 'export VMWARE_VSPHERE_USERNAME="root"' >> ~/.bashrc
echo 'export VMWARE_VSPHERE_PASSWORD="Srv@1234"' >> ~/.bashrc
source ~/.bashrc