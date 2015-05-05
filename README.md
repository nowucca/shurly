shurly
======

The shurly project provides Java libraries to perform URI shortening.

Shurly is designed as a small collection of Java modules, packaged into libraries that support multiple URI shortening schemes, as well as a library providing a high-performance client/server for over-the-network uri shortening use cases.

Cloning this project
--------------------

You can clone the repository wherever you want. 

    git clone https://github.com/nowucca/shurly.git
    
    
Building this project
---------------------

`mvn clean verify` will build the project

[![Build Status](https://travis-ci.org/nowucca/shurly.svg?branch=master)](https://travis-ci.org/nowucca/shurly)

Running the Example Client and Server
-------------------------------------

Open up two tabs in a terminal. 

In the first tab, type:
    
    $ mvn package -Pserver
    
You see the server start up:

    Shurley Server (c) 2014 Steven Atkinson.  All Rights Reserved.
    
    Available URI Managers are: 
    	 com.nowucca.shurly.core.basic.BasicURIManager
    	 com.nowucca.shurly.core.base32.Base32URIManager
    
    Using URI Manager: com.nowucca.shurly.core.basic.BasicURIManager
    
    
    Started in 0.116 seconds.

    
In the second tab, type:

    $ mvn package -Pclient
    
You should see the client start up:

    Shurley Client (c) 2014 Steven Atkinson.  All Rights Reserved.
    
    Available commands: 
      shrink <uri>      -- shrinks the uri provided
      follow <uri>      -- follows the uri provided
      list              -- list the shortenings so far
      bye               -- quit
      quit              -- quit
    
    shurly> 

You can now shrink urls, and follow them:

    shurly> shrink http://dvd.netflix.com
    Sent SHRINK.v1 (0) http://dvd.netflix.com
    Received SHRUNK.v1 (0) shortURI="http://nowucca.com/shurly/basic/a" longURI="http://dvd.netflix.com".
    shurly> shrink http://www.kaazing.com
    Sent SHRINK.v1 (1) http://www.kaazing.com
    Received SHRUNK.v1 (1) shortURI="http://nowucca.com/shurly/basic/b" longURI="http://www.kaazing.com".
    shurly> list
    
    http://www.kaazing.com -> http://nowucca.com/shurly/basic/b
    http://dvd.netflix.com -> http://nowucca.com/shurly/basic/a
    
    shurly> follow http://nowucca.com/shurly/basic/a
    Sent FOLLOW.v1 (2) http://nowucca.com/shurly/basic/a
    Received SHRUNK.v1 (2) shortURI="http://nowucca.com/shurly/basic/a" longURI="http://dvd.netflix.com".
    shurly> bye
    Bye.

The "sent" and "received' log lines show the type, version and message sequence number of the custom protocol commands going over the network from the client to the server and back.
