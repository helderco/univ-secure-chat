# Secure Chat

This Java application allows secure communication between peers using JacORB,
which is a CORBA implementation in Java. This means it's possible to build
clients in other languages and platforms.

## Steps

In order to run the project one must first:
- Clone this project
- Get the JacORB Utilities
- Build with Maven

## Build

If you use Netbeans, the Build Project and Clean and Build Project buttons will
already package a .jar with all dependencies.

On the command line, the equivalent is to do:

`$ mvn [clean] compile assembly:single`


### Jaco

If you see the documentation for JacORB, they use the `jaco` utility in their
examples. This project comes with a version that will work with the jar which
comes with all dependencies.

E.g., to run the server:

`$ ./jaco org.siriux.chat.Server`


### JacORB utilities

There are other utilities that come with JacORB exemplified in the documentation,
but these don't come with the Maven artifacts. One example is `ns`, to run the
Name Service.

Here's how to build them.

1. Clone the Git repository in https://github.com/JacORB/JacORB, using your
   favorite protocol.

2. Extend your search path ($PATH) to include the `bin` subdirectory of JacORB
   so that the utilities can be found (more details below).

2. Run `ant scripts` to build the scripts.


#### PATH

When using these utils, most of them refer to the script `jaco`, but we must use
our own version, which is in the root of this project for our app to work
seamlessly. So you need to have this project's root in your PATH **before** the
`bin` directory from JacORB. This way, our own version of `jaco` will be used
instead.

One recommendation, if you don't want to add the project to your PATH, is to use
the current directory instead. This assumes you'll want to run the utilities
from the root of the project.

`export PATH=$PATH:.:$HOME_JACORB/bin`

Where `$HOME_JACORB` is the absolute path to your JacORB clone.


## Run Demo

To run the Sum demo run the following in separate windows.

`$ ns`

`$ jaco org.siriux.chat.Server`

`$ jaco org.siriux.chat.Client <num1> <num2>`

Choose any two numbers, you should get their sum in return.

Note: if you don't have `ns`, replace it for `jaco org.jacorb.naming.NameServer`.
