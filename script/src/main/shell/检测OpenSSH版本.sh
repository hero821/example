#!/bin/bash

get_openssh_version(){
    data=$(ssh -v "$1" 22 2>&1 | grep "remote software version" | awk '{print $9}')
    echo "$1:${data}"
}

ip=(
    192.168.111.228
)
for i in "${ip[@]}"
do
    get_openssh_version "${i}"
done