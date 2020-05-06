Overlord
========

![Build Status](https://github.com/dotStart/Overlord/workflows/Build/badge.svg)
[![Lines of Code](https://torchmind.com/sonarqube/api/project_badges/measure?project=overlord&metric=ncloc)](https://torchmind.com/sonarqube/dashboard?id=overlord)
[![Quality Gate Status](https://torchmind.com/sonarqube/api/project_badges/measure?project=overlord&metric=alert_status)](https://torchmind.com/sonarqube/dashboard?id=overlord)


Quick and simple automated game server deployment.

**This project is under heavy development and will introduce breaking changes without
prior warning or promises of backwards compatibility -  Use at your own risk**

Running in Standalone Mode
--------------------------

For instance, to start a Minecraft server using the default server definition:

```shell script
$ java -jar overlord-agent.jar standalone examples/minecraft.yaml
```

Most aspects of the definition file are self-explanatory. Refer to the
[examples](examples) directory for comprehensive configuration examples.

Running in RPC Mode
-------------------

When executed in RPC mode, the Overlord agent provides you with the ability to remotely
provision and control a server instance of your choosing.

**Note:** This feature is primarily aimed at developers who wish to leverage the server deployment
capabilities within an automated environment of their choosing and thus lacks user facing
documentation. For a full listing of available methods, refer to th
 [gRPC service definitions](overlord-agent-api/src/main/proto).
 
```shell script
$ java -jar overlord-agent.jar rpc-server
```

For maintenance and evaluation purposes, the following RPC client commands are provided as well:

```shell script
$ java -jar overlord-agent.jar rpc provision examples/minecraft.yaml
$ java -jar overlord-agent.jar rpc start
$ java -jar overlord-agent.jar rpc stop
```

**Note:** The RPC commands are provided as a convenience and do not provide any stability guarantee
or fancy error handling. If you wish to automate a deployment process, please make use of the
provided gRPC definitions (these definitions may also be used to generate bindings for languages
outside of the Java ecosystem).

License
-------

```
Copyright (C) 2020 Johannes Donath <johannesd@torchmind.com>
and other copyright owners as documented in the project's IP log.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
