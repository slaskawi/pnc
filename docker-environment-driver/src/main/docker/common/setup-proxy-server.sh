#!/bin/bash
#
# JBoss, Home of Professional Open Source.
# Copyright 2014 Red Hat, Inc., and individual contributors
# as indicated by the @author tags.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# sets proxy server in maven settings.xml file from environment variables
# proxyIPAddress
# proxyPort

file=/usr/share/maven/conf/settings.xml

if [[ ! -z "$proxyServer" ]]; then
    echo "Configuring Maven proxy"
    sed -i "s/\${proxyServer}/${proxyServer}/" $file
    sed -i "s/\${proxyPort}/${proxyPort}/" $file
    sed -i "s/\${proxyUsername}/${proxyUsername}/" $file
    sed -i "s/\${nonProxyHosts}/${nonProxyHosts}/" $file
    sed -i "s/\${isHttpActive}/${isHttpActive}/" $file
else
    echo "Disabling Maven proxy"
    sed -i "s/\${isHttpActive}/false/" $file
    # Dummy port to stop maven complaining
    sed -i "s/\${proxyPort}/80/" $file
fi
