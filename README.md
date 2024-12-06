# Streaming Video Manager

### Version 1.0.0

A command-line utility to select videos on a remote server so you can then download them. The application allows you to easily connect yourself remotely to the server, help you choose a video and then plays it for you with your system-default video player.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
    - [Examples without Docker](#examples-without-docker)
    - [Examples with Docker](#examples-with-docker)
    - [Command Summary](#command-summary)
- [Building from Source](#building-from-source)
- [Credits](#credits)

---

## Features
- **TCP connection**: TCP to interact with the server (connect, upload, delete, list, download).
- **Multithreading**: The server can manage up to 10 (by default) remote connections at the same time.
- **Secure authentication**: Secure management of user entry with regexes.
- **User-friendly CLI**: Easy to use with clear options and commands.
- **Thread-safe**: The server and its resources are thread-safe and can manage multiple clients at the same time.

---

## Requirements
- **Java**: You need to have Java 21 installed.
- **Maven**: Ensure you have Maven installed to manage dependencies and build the project.
- 
---

## Installation

### Clone the Repository

To get started, you can clone the project repository to your local machine:

```bash
git clone https://github.com/HEIG-VD-S3-PW/DAI-LAB-03

cd DAI-LAB-03
```

Build the Project with Maven

Once you have cloned the project, use Maven to build it:

```bash
mvn clean install
```

After a successful build, the jar file will be created in the target/ directory:

```bash
target/DAI-LAB-03-1.0-SNAPSHOT.jar
```

## Usage

The application is a CLI tool that help you choose videos to download. You need to identify yourself first before choosing between the available videos.
Available Commands

    client: Start the client connection to the server.
    server: Start the server.

Shared options

    -p, --port: Port to use (default: 1986).
    -h, --help: Displays the help for the application and exit.
    -V, --version: Prints version information and exit.

Server option

    -c, --clients: Maximum number of clients to accept (default: 10).

Client option

    -H, --host: Host to connect to (For the client connection).

Help Command

You can use the help command to get information about the available options and commands:

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar --help
```

Output:

```vbnet
======= Amar Streaming Platform =======

Download, upload and remove videos from/to a remote server

Usage: DAI-LAB-03-1.0-SNAPSHOT.jar [-hV] [COMMAND]

Description:
A command-line utility to stream videos from a remote server.

Options:
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

Commands:
  Client  Start the client to connect to the streaming server.
  Server  Start the server to accept remote connections

Credits: Tristan Baud, Arno Tribolet and Mathieu Emery
```

The commands "Client" and "Server" also have a custom help page:

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar Client --help
```

```vbnet
=== Client Command ===

Usage: DAI-LAB-03-1.0-SNAPSHOT.jar Client -H=<host> [-p=<port>]

Description:
Start the client to connect to the streaming server.

Once connected the following commands are available:
DELETE <id>     Delete a video that you select with it's number.
LIST            Show all videos from the server
DOWNLOAD <id>   Download a specific video by using it's number
UPLOAD          Upload a new video

Options:
  -H, --host=<host>   Host to connect to.
  -p, --port=<port>   Port to use (default: 1986).

Credits: Tristan Baud, Arno Tribolet and Mathieu Emery

```

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar Server --help
```

```vbnet
=== Server Command ===


Usage: DAI-LAB-03-1.0-SNAPSHOT.jar Server [-p=<port>]

Description:
Start the server to accept remote connections

Options:
  -p, --port=<port>   Port to use (default: 1986).
  -c, --clients=<clients>   Maximum number of clients to accept (default: 10).

Credits: Tristan Baud, Arno Tribolet and Mathieu Emery

```

## Examples without Docker
### Starting the server connection

To start the server and make him listen for incoming connections :

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar server
```

Output:

```arduino
Server listening for connections on port: 1986
```

### Starting a client connection

To create a client connection on the remote server so you can identify yourself:

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar Client -H localhost
```

Output:

```arduino
Welcome to the Amar Streaming Platform !
Enter your pseudo:
```

The client will then have to enter his pseudo (Will ask you to re-enter it if you don't):

```arduino
Welcome to the Amar Streaming Platform !
Enter your pseudo: 
Enter your pseudo: toto
Enter your email:
```

After that, the client must enter a valid email (checked by a regex) to access the list of video:

```arduino
Welcome to the Amar Streaming Platform !
Enter your pseudo: 
Enter your pseudo: toto
Enter your email: toto@exemple.com
```

After that, the CONNECT command ask for the connection and, if it is accepted, the client will be able to use the following commands at will:

- DELETE <id> : Delete the video with the <id> number 
- LIST: List all videos on the server
- DOWNLOAD <id> : Download the video with the <id> number 
- UPLOAD: Upload a video

Example with the command LIST:

```bash
> LIST
1) 3 Minute Timer - Displays a timer from 3 minutes to 0
2) Google Office tour - Visit of Google's building
3) L'entretien - Choss - Vidéo de Choss sur un entretien
4) Le Clown - Choss - Vidéo de Choss sur un clown
5) Why is Switzerland home to so many billionaires - Documentary on Switzerland's billionaires
>
```

## Examples with Docker

You can also use Docker to run the application. First, create the Docker image and the network:

```bash
docker build -t ammar-app .
docker network create ammar-net
```

Then, start the server and the client in two different terminals:

```bash
docker run -p 1986:1986 -v ./server_data:/app/server_data \
  --network ammar-net --name ammar-server ammar-app Server
```

```bash
docker run -it -v ./client_data:/app/client_data \
  --network ammar-net ammar-app Client --host ammar-server --port 1986
```

Be sure to have the folders `server_data` and `client_data` in the same directory as the Dockerfile.

### Cleaning up

After you are done, you can stop and remove the containers and the network:

```bash
docker stop ammar-server
docker rm ammar-server
docker network rm ammar-net
```


---

## Command Summary
| Command       | 	Description                                     |
|---------------|--------------------------------------------------|
| client        | Start the client connection to the server.       |
| server        | 	Start the server.                               |
| -H, --host    | 	Host to connect to (For the client connection). |
| -p, --port    | 	Port to use (Default: 1986)                     |
| -h, --help    | 	Show help message and exit.                     |
| -V, --version | 	Print version information and exit.             |
| -c, --clients | 	Maximum number of clients to accept (Default: 10)|

---

## Building from Source

If you want to contribute or modify the application, follow these steps to build the project from the source.

Clone the repository:

```bash
git clone https://github.com/HEIG-VD-S3-PW/DAI-LAB-03

cd DAI-LAB-03
```

Build the project: Use Maven to build the project and generate the JAR file.

```bash
mvn clean install
```

Run the application: Once built, you can run the application using:

```bash
java -jar target/DAI-LAB-03-1.0-SNAPSHOT.jar
```

Testing the application: You can also run the tests using Maven:

```bash
mvn test
```

---

## Adding new commands

To add a new command, you need to create a new class that extends the Command class and implement the validate, execute and receive commands. You can then add the command to the CommandRegistry class.

```java
@Override
public void validate(String[] args) throws CommandException {
    // Check the arguments and throw an exception if they are invalid
}

@Override
public CommandResponse execute(User user, StreamingVideo streamingVideo, String[] args) {
    // SERVER SIDE : Execute the command and return a response to the client
}

@Override
public void receive() {
    // CLIENT SIDE : Receive the response from the server
}
```

```java
public CommandRegistry(BufferedReader in, BufferedWriter out) {
    // ...
  registerCommand(new NewCommand());
}
```

Now your command is available to use in the application.

__Moreover, if you choose to contribute to the project by adding a new command, you have to create diagrams to explain the new command, how it works, and the interactions between the client and the server.__

---

## Credits

This project was developed by Tristan Baud ([NATSIIRT](https://github.com/NATSIIRT)), Arno Tribolet (([arnoheigvd](https://github.com/arnoheigvd)), and Mathieu Emery ([mathieuemery](https://github.com/mathieuemery)) as part of a DAI (Development of internet applications) lab project.


---