Overlord
========

![Continous Testing](https://github.com/dotStart/Overlord/workflows/Build/badge.svg)

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
