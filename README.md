# Secure Chat

This Java application allows secure communication between peers using JacORB,
which is a CORBA implementation in Java. This means it's possible to build
clients in other languages and platforms.

## Build

You must have Maven to build the project.

If you use Netbeans, the Build Project and Clean and Build Project buttons will
already package a .jar with all dependencies.

On the command line, the equivalent is to do:

`$ mvn [clean] compile assembly:single`


## Jaco

To run the application, use the utility `jaco`. Run it with no arguments to get a list of available options.

`$ ./jaco`


## Run Demo

To run a demo with SSL enabled, first execute the `demo.sh` script to generate the certificates and then run the following in separate windows.

`$ jaco -k server.jks ns`

`$ jaco -k server.jks Server`

`$ jaco -k alice.jks Client Alice`

`$ jaco -k bob.jks Client Bob`

The `-k` argument defines a keystore and activates SSL feature, with mutual authentication.


## Turn on SSL

To use the default `.keystore` file, you can turn SSL support on with the `-s` argument.

`$ jaco -s ns`

Or with an environment variable:

`$ export CHAT_SSL=true`

`$ jaco ns`


## Run in separate machines

The clients and server must have access to a naming service to resolve the objects' addresses. This is basically a file that they have to access, and by default it's saved to `/tmp/NS_Ref`.

To run the application in different machines, the name service must be accessible to the clients and server, either through a shared filesystem or a web server.

### Web server

To use a web server for providing the name service:

`$ jaco -i </path/to/www/>NS_Ref ns`

Then, any client/server is run with:

`$ jaco -N <ip-of.remote.web-server> {Server|Client}`

### Shared filesystem

On a shared filesystem the clients and server are run with:

`$ jaco -n file:</path/to/NS_Ref> {Server|Client}`


## Jar location

By default, jaco attempts to find the .jar file in `target/chat-1.0-jar-with-dependencies.jar`, and if not found, the same .jar in the local dir. You can also specify an alternate location:

`$ jaco -j <path/to/chat.jar> ns`

Or with an environment variable:

`$ export CHAT_JAR=<path/to/chat.jar>`

`$ jaco ns`


## Logging

The application logs can be sent to the screen, to a file or both. The logging properties can be configured at runtime. By default, jaco will attempt to find a `logging.properties` file to use.

 If not in the local directory it will extract the one that comes with the .jar file. To override this and choose your own:

`$ jaco -l ~/.schat-logging ns`

Or with an environment variable:

`$ export LOG_PROPS=~/.schat-logging`

`$ jaco ns`

### Filtering logs

The first time you run `jaco` it will extract the `logging.properties` file from the .jar file to the local dir. Open that file to learn how you can filter the output that you see on screen.

### Logging to file

By default screen logging is set to INFO level, but the file logging is set to ALL, which includes fatal errors and exception stack traces.

By default, the log file is saved to `/tmp/schat.log`.


## NAT and firewalls

Network Address Translation (NAT) frequently causes a lot of problems if internal CORBA objects need to be accessed from outside the NAT network. Cause of these problems is that object IORs contains host’s IP address but internal (inside the NAT) IPs are not accessible from outer network.

Simplest solution is using the `-x` argument and DNS names instead of IP addresses. E.g. we have the 192.168.10.* network managed by NAT. Its gateway has inner IP: 192.168.10.1 and outer IP: 10.30.102.67.

To make server object behind the NAT accessible from outer network, the following configuration steps need to be done:
- Check that DNS name for the host is set. It should be mapped e.g. to the 192.168.10.128 IP inside NAT and to 10.30.102.67 outside the NAT.
- Choose and define the server object’s port (this will allow to easy port mapping by NAT and firewall).
- Use the `-x` argument to define the proxy host and `-p` for the port.

`$ jaco -x chat.siriux.org -p 5555 Server`

Setup port forwarding in NAT and firewall according to their configuration guides. Note that port numbers should be the same, e,g. if server uses 15242 port it should have bound to the 15242 gateway port.


## More information

Problem set is in `docs` (portuguese).
