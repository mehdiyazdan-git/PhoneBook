#!/bin/bash

# Set default environment variables for application configuration

# Set server port if not already set
export SERVER_PORT=${SERVER_PORT:-8081}

# Set database connection details if not already set
export SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL:-"jdbc:postgresql://localhost:5432/phonebook_db"}
export SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-postgres}
export SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-postgres}

# Set VMware vSphere API details if not already set
export VMWARE_VSPHERE_URL=${VMWARE_VSPHERE_URL:-"https://192.168.3.2/sdk"}
export VMWARE_VSPHERE_USERNAME=${VMWARE_VSPHERE_USERNAME:-root}
export VMWARE_VSPHERE_PASSWORD=${VMWARE_VSPHERE_PASSWORD:-"Srv@1234"}

echo "Environment variables set:"
echo "SERVER_PORT=$SERVER_PORT"
echo "SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL"
echo "SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME"
echo "SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD"
echo "VMWARE_VSPHERE_URL=$VMWARE_VSPHERE_URL"
echo "VMWARE_VSPHERE_USERNAME=$VMWARE_VSPHERE_USERNAME"
echo "VMWARE_VSPHERE_PASSWORD=$VMWARE_VSPHERE_PASSWORD"
