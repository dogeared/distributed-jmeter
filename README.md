## Digital Ocean + Distributed JMeter

The purpose of this project is to demonstrate the Digital Ocean (DO) [API](https://developers.digitalocean.com/documentation/v2/) capabilities by 
launching a number of [droplets](https://www.digitalocean.com/help/) and running a [JMeter](http://jmeter.apache.org/) test plan through them.
   
JMeter is a powerful testing library. Aside from being able to run simple and complex
multi-step test plans, it can be used in a distributed fashion where it coordinates the
execution of the test plan across (potentially hundreds or thousands) of machines.
   
This is a Spring Boot application and, when launched with the right parameters, it will do
the following:

1. Create a specified number of JMeter server droplets
2. Add those JMeter server droplets to a specified DO firewall definition
3. Launch JMeter in server mode on each of those droplets
4. Create a JMeter client droplet
5. Add the JMeter client droplet to a specified DO firewall definition
6. Install a JMeter test plan on the client droplet
7. Launch JMeter in client mode and run the test plan across all server droplets

## Setup

1. [Create](https://cloud.digitalocean.com/registrations/new) a DO account
2. [Create](https://cloud.digitalocean.com/settings/api/tokens) a DO token for use with the API
3. Setup a [firewall](https://cloud.digitalocean.com/networking/firewalls)
    * Create an inbound TCP rule for SSH (port 22)
    * Create an inbound TCP rule for RMI (port 1099)
    * Create an inbound TCP rule for local-RMI (port 4040 - more on this below)
    * Make note of the firewall id in the url for use later 
    (It will be something like: `028ddd0d-5d3d-4360-8eb9-0351d94f30d5`)
4. Add an SSH public key to your [account](https://cloud.digitalocean.com/settings/security)
    * Make note of it's fingerprint for use later

## Build

`mvn clean install`

## Run

This application exposes an API to interact with the DO droplets that are created
and has command line switches for controlling the number and the size of JMeter
server and client droplets.

### Basic Launch

At a minimum, you'll need to specify DO Token to launch the Spring Boot application.

```
DO_TOKEN=<valid token from DO> target/distributed-jmeter-0.0.1-SNAPSHOT.jar
```

After Spring Boot starts, you can hit its API. 
Here's some examples, using [HTTPie](https://httpie.org):

List the IP addresses of all droplets: `http localhost:8080/api/v1/droplet_ips`

```
[
    "138.197.96.81",
    "138.197.126.211",
    "138.197.126.193",
    "138.197.126.213",
    "138.197.126.246",
    "138.197.120.177",
    "138.197.114.8",
    "138.197.126.144",
    "138.197.120.160",
    "138.197.126.186",
    "138.197.126.187"
]
```

List all the configuration information for all droplets (produces a lot of output):
`http localhost:8080/api/v1/droplets`

```
[
	{
		"id": 54653734,
		"name": "jmeter-client",
		...
		"features": [
            ...
		],
		"tags": [
			"jmeter-client"
		],
		"created_at": "2017-07-11T00:17:11Z",
        ...
		"region": {
			"name": "New York 3",
			"slug": "nyc3",
            ...
		}
	},
    ...
]
```

List a specific attribute for all droplets: `http localhost:8080/api/v1/droplets_attribute?attribute=name`

```
[
    "jmeter-client",
    "jmeter-server-1",
    "jmeter-server-2",
    "jmeter-server-3",
    "jmeter-server-4",
    "jmeter-server-5",
    "jmeter-server-6",
    "jmeter-server-7",
    "jmeter-server-8",
    "jmeter-server-9",
    "jmeter-server-10"
]
```

Look [here]() for a complete API reference. *NOTE*: in this context, 
it is this Spring Boot application's API we are talking about and not the DO API.

### JMeter Launch

If you look at the output from the basic launch, you will note that there's help for
the command-line interface:

```
You can pass parameters to this Spring Boot Application to launch JMeter instances:
Option                       Description
------                       -----------
--client-only                Only launch JMeter Client
--client-size <String>       The DigitOcean size of the client droplet. Ex:
                               512mb, 1gb, 2gb, etc.
--server-droplets <Integer>  Number of JMeter Server Droplets to create
--server-only                Only launch JMeter Servers
--server-size <String>       The DigitOcean size of each droplet. Ex: 512mb,
                               1gb, 2gb, etc.
```

In order to take advantage of these parameters and launch a distributed JMeter test
plan, you'll need to provide a few more environment variables:

| environment variable       | purpose |
|----------------------------|---------|
| DO_TOKEN                   | DO API Token |
| DO_IMAGE_ID                | string or int of DO image id to launch for each droplet |
| DO_FIREWALL_ID             | DO firewall id to add each droplet to |
| DO_CONFIG_FILE             | fully qualified path to yml file per [docs]() for post launch droplet setup |
| SSH_PUBLIC_KEY_FINGERPRINT | fingerprint of public key installed at DO. Each droplet will be setup with this public key |
| SSH_PRIVATE_KEY_FILE       | fully qualified path to private ssh key file that matches public fingerprint |
| JMETER_TEST_PLAN_FILE      | fully qualified path to the JMeter .jmx test plan file that will be executed |

Here's an example of a command that will launch an entire distributed environment 
with 2 JMeter servers and the JMeter client:

```
DO_TOKEN=<DO API token> \
DO_IMAGE_ID=<DO Image id. default: 26136050 (which is: ubuntu-16-04-x64)> \
DO_FIREWALL_ID=<DO firewall id> \
DO_CONFIG_FILE_=<full path to yml file. ex: ~/do_config.yml> \
SSH_PUBLIC_KEY_FINGERPRINT=<colon delimited ssh public key fingerprint> \
SSH_PRIVATE_KEY_FILE=<full path to ssh private key file> \
JMETER_TEST_PLAN_FILE=<full path to jmeter test plan file. ex: ~/test_plan.jmx> \
target/distributed-jmeter-0.0.1-SNAPSHOT.jar \
--server-droplets 2 --server-size 512mb --client-size 1gb
```

*Note*: It will take some time to fire up all the instances. Throughout the startup,
the API exposed by the Spring Boot app is available.

*Note*: Your client will need to be a bigger size than the server droplets to support all
the networking and coordination it needs to do. `1gb` is adequate for up to 10 server
droplets. In testing with 99 512mb server droplets, and 8gb client was required.

You will see output like this:

```
09:03:17.776  INFO : Creating 2 JMeter Server Droplets
09:03:17.784  INFO : ... Still working on creating JMeter Server Droplets ...
09:03:23.792  INFO : Done Creating 2 JMeter Server Droplets
09:03:23.792  INFO : Waiting for 2 JMeter Server Droplets to become active
09:03:23.793  INFO : ... Still working on making JMeter Server Droplets active ...
09:04:18.833  INFO : All 2 JMeter Server Droplets are active
09:04:18.833  INFO : Adding JMeter Servers to Firewall
09:04:18.834  INFO : ... Still adding JMeter Servers to Firewall ...
09:04:20.839  INFO : Added JMeter Servers to Firewall.
09:04:20.839  INFO : Starting JMeter Servers
09:04:20.840  INFO : ... Still starting JMeter Servers ...
09:04:30.849  INFO : All JMeter Servers are started
09:04:30.849  INFO : IP Addresses: 159.203.137.53,162.243.185.231
09:04:30.849  INFO : Creating JMeter Client Droplet
09:04:30.849  INFO : ... Still working on creating JMeter Client Droplet ...
09:04:32.850  INFO : Done Creating JMeter Client Droplet
09:04:32.850  INFO : Waiting for JMeter Client Droplet to become active
09:04:32.851  INFO : ... Still working on making JMeter Client Droplet active ...
09:05:07.875  INFO : JMeter Client Droplet is active
09:05:07.875  INFO : Adding JMeter Client to Firewall
09:05:07.876  INFO : ... Still adding JMeter Client to Firewall ...
09:05:08.881  INFO : Added JMeter Client to Firewall.
09:05:08.881  INFO : Copying test plan for JMeter Client
09:05:08.881  INFO : ... Still copying test plan to JMeter Client ...
09:05:12.888  INFO : Copied test plan to JMeter Client
09:05:12.889  INFO : Starting JMeter Client
09:05:12.889  INFO : ... Still starting JMeter Client ...
09:05:17.890  INFO : JMeter Client is started
09:05:17.893  INFO : Started DistributedJmeterApplication in 122.886 seconds (JVM running for 123.327)
```

You can hit the available API from the Spring Boot application to get back information on
the test environment:
 
```
http localhost:8080/api/v1/droplets_attributes?attributes=name,dropletNetworks
```

```
[
    {
        "dropletNetworks": {
            "v4": [
                {
                    "ip_address": "159.203.137.53",
					...
                }
            ],
            "v6": []
        },
        "name": "jmeter-server-1"
    },
    {
        "dropletNetworks": {
            "v4": [
                {
                    "ip_address": "162.243.185.231",
					...
                }
            ],
            "v6": []
        },
        "name": "jmeter-server-2"
    },
    {
        "dropletNetworks": {
            "v4": [
                {
                    "ip_address": "159.203.133.164",
					...
                }
            ],
            "v6": []
        },
        "name": "jmeter-client"
    }
]
```

If you ssh to the `jmeter-client` droplet, you can monitor the progress of the test run:
 
```
ssh root@<jmeter client ip>
```

/root/jmeter-client.log (log output from jmeter):
```
Creating summariser <summary>
Created the tree successfully using /root/xxxx.jmx
Configuring remote engine: 159.203.137.53
Configuring remote engine: 162.243.185.231
Starting remote engines
Starting the test @ Wed Jul 12 16:05:17 UTC 2017 (1499875517510)
Remote engines have been started
Waiting for possible Shutdown/StopTestNow/Heapdump message on port 4445
summary +   2802 in 00:00:06 =  433.7/s Avg:    66 Min:     9 Max:  1053 Err:     0 (0.00%) Active: 102 Started: 102 Finished: 0
...

```

/root/log_remote.jtl (raw log output accumulated from jmeter servers):
```
timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,sentBytes,grpThreads,allThreads,Latency,IdleTime,Connect
1499876018809,2384,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,2384,0,0
1499876021193,95,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,94,0,9
1499876021288,20,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,20,0,0
1499876021308,12,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,12,0,0
1499876021321,13,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,13,0,0
1499876021334,16,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,16,0,0
1499876021350,11,ipify,200,OK,Thread Group 1-84,text,true,,220,129,2,2,11,0,0
1499876019363,2144,ipify,200,OK,Thread Group 1-83,text,true,,220,129,2,2,2144,0,0
```

## TODO

* Configure region. Currently hard-coded to NYC3
    * specify a single region
    * specify a collection of regions and round-robin the jmeter server creation
* Support both image name and image id. Currently only numeric image id is supported