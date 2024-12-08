# Video Manager Application Protocol

## 1. Overview
Video Manager Application (VMA) is a protocol for retrieving and uploading video files to a server. Its aim is to 
simplify video sharing between users.

## 2. Transport protocol
VMA is a client-server protocol for listing, uploading, downloading and deleting videos.  
VMA uses the __TCP protocol__ for these exchanges.  
The default port is __1986__.

The exchanges between client and server are as follows:
- Sending a command and its arguments from the client to the server.
- Transfer of data in the form of text encoded in Base64. These exchanges are from client to server during an upload and
  from server to client during a download. The delimiters are ``END_OF_UPLOAD`` and ``END_OF_DOWNLOAD`` respectively.
- Sending a code followed by a message from the server to the client. The code can represent a confirmation or an error.
  The message describes the code.

For all commands, a server-side validity check is performed on the command and its arguments. If the execution of the 
command encounters a problem, the server will send an error to the client. In the event of an error, the client will 
display the message.  

If it manages to connect to the server, the client must connect to the service before it can send any more
messages. It is __the client that initiates the connection__. Using the ``CONNECT`` command allows you to launch an 
interactive authentication managed by the client. The client asks the user for and verifies his username and email 
address. It then builds the command and sends it to the server.  
Alternatively, it is possible to send ``CONNECT <Username> <Email>`` directly, in which case management is carried out 
solely by the server.
The server returns a confirmation.

The user can request the list of videos. The server will then send confirmation code and a list of the videos
that are available.

The user can request to download a video. The server returns a confirmation and starts sending the data. When the client
receives confirmation, it starts receiving the data. The end of the transmission is marked by its delimiter.

The user can ask to upload a video to the server. Using the ``UPLOAD`` command launches an interactive interface for 
setting the upload parameters managed by the client. The client asks the user and checks the title, description and path
of the video. It then builds the command and sends it to the server.  
The server returns a confirmation so that the transfer can begin. At the end of the transfer, the client sends the 
defined delimiter and the server returns a confirmation.

The user can request that a video be deleted. The server checks that the video exists and that it is available for 
deletion. If so, it deletes it. Otherwise it returns an error. It must not be possible to delete a video being uploaded
by another user.

When the client has finished using the service, it uses the ```QUIT``` command to end the connection. The user is then is 
then removed from the list of users and the server returns a confirmation.  

## 3. Messages

In all cases, the server will respond with a code and a message.  
- ``` <CommandResponseCode> <Message>```

Codes list: 
- 200 (OK)
- 500 (ERROR)
- 404 (NOT_FOUND)
- 403 (FORBIDDEN)
- 401 (UNAUTHORIZED).

For all commands except ```CONNECT``` and ```QUIT```, the server will always respond with the code ```401``` 
(UNAUTHORIZED) and with the message ```You have to be connected to execute this command``` if the client is not connected.
### Connection to the server

The client asks the server to connect.  

#### Message

```CONNECT <pseudo> <email>```

#### Response

- For code ```200``` (OK). 
  - The connection is confirmed:
    ```Connection successfull```

- For code ```500``` (ERROR)
    - If the username is invalid:
      ```Invalid pseudo```
    - If the email address is invalid:
      ```Invalid email address```
    - If the user already exists:
      ```User already exists```
    - If the number of arguments is invalid:
      ```Server error : The connect command expects exactly two arguments (CONNECT <pseudo> <email>)```

### Listing videos

The client asks the server for the list of videos.

#### Message

```LIST```

#### Response

- For code ```200``` (OK) 
  - You will receive a list of available videos: 
    ```1,<titre_1>,<description_1>;2,<titre_2>,<description_2>;...;n,<titre_n>,<description_n>;```

- For code ```404``` (NOT_FOUND)
    - No videos stored on the server:
      ```No videos found```

### Downloading a video

The client asks the server to download a video. This command takes place in two stages. Firstly, the server confirms 
that the video is available by sending a ```200``` code (OK) and the title of the video. Next, the server sends the 
video data __in the form of a chunk stream encoded in Base64__.  
The stream is considered complete when the server sends the ```END_OF_DOWNLOAD``` delimiter. When the client receives this
delimiter, it stops reading the data, and the download is considered complete.  

#### Message

```DOWNLOAD <videoChoice>```

#### Response

- For code ```200``` (OK).
    - Confirmation that the download has started: ```<video_title>```
- For code ```404``` (NOT_FOUND)
    - The requested video does not exist: ```Invalid/unknown video choice```
- For code ```403``` (FORBIDDEN)
    - The video is currently unavailable: ```Video is currently being deleted or is unavailable```
- For code ```500``` (ERROR)
    - If the number of arguments is invalid:
      ```Server error : The download command expects exactly one argument (DOWNLOAD <videoChoice>)```

#### Exception

In the event of an exception during the transfer, the server will return a code ```500``` (ERROR) followed by the 
message ```Error while downloading video: <Exception_message>```

### Uploading a video

The client asks the server to upload a video.  

#### Message

```UPLOAD <Base64_titre> <Base64_description>```

#### Response

The response takes place in three stages. The server confirms whether or not it has received the information to start 
the transfer. After this confirmation, the server prepares to receive the video data __in the form of a stream sent by 
chunk and encoded in Base64__.  
The stream is considered complete when the client sends the ```END_OF_UPLOAD`` delimiter. The server then confirms 
whether the transfer was successful, and the video is then available.

- Validation to start the upload
    - For code ```200``` (OK)
        - Confirmation that the server is ready to receive the transmission: ```Ready to receive video```
    - For code ```500``` (ERROR)
        - Problem in encoding the title or description: ```Invalid Base64 encoding```
        - If the title already exists: ```Video title already exists```
        - If the number of arguments is invalid:
          ```Server error : The upload command expects title and description```
    - For code ```401``` (UNAUTHORIZED)
        - The user is not connected: ```You have to be connected to execute this command```

- Confirmation of upload
    - For code ```200``` (OK)
        - Confirmation that the upload completed successfully ```Video uploaded successfully```

#### Exception

In the event of an exception during the upload, the server will return a code ```500``` (ERROR) followed by the message
```Error while uploading video : <Exception_message>```

### Deleting a video

The client asks the server to delete a video.

#### Message

```DELETE <videoChoice>```

#### Response

- For code ```200``` (OK).
    - Confirmation of deletion: ```Video deleted```
- For code ```404``` (NOT_FOUND)
    - The requested video does not exist: ```Video not found```
- For code ```403``` (FORBIDDEN)
    - The video is currently unavailable: ```Video is currently being downloaded by other users```
- For code ```500``` (ERROR)
    - If the number of arguments is invalid:
      ```Server error : The delete command expects exactly one argument (DELETE <videoChoice>)```

#### Exception

In the event of an exception during deletion, the server will return a code ```500``` (ERROR) followed by the message 
```Server error: <Exception_message>```.  

### Disconnecting from the server

The client disconnects from the server.

#### Message

```QUIT```

#### Response

- For code ```200``` (OK).
    - Confirmation of disconnection: ```See you soon :)```

_The server will always send a disconnection confirmation, even if the user was not logged in._

## Examples

### Default operations

#### Downloading
![Image](examples/example1.svg)

#### Uploading
![Image](examples/example6.svg)

#### Deleting
![Image](examples/example2.svg)

### Error handling

#### Several errors

![Image](examples/example7.svg)

#### Unauthenticated commands

![Image](examples/example4.svg)

### Concurrency Example

#### Without any concurrent error

![Image](examples/example3.svg)

#### With concurrent error
![Image](examples/example5.svg)
