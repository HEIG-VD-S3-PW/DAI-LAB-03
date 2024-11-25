# DAI-LAB-03

## 1. Overview  
Le protocole DAI-LAB-03 est un protocole permettant de streamer une vidéo depuis le serveur en utilisant le lecteur de 
media [VLC](https://www.videolan.org/vlc/index.fr.html) du client.


## 2. Transport protocol 




This section defines the transport protocol used by the application protocol:

    What protocol(s) is/are involved? On which port(s)?
    How are messages/actions encoded?
    How are messages/actions delimited?
    How are messages/actions treated (text or binary)?
    Who initiates/closes the communication?
    What happens on an unknown message/action/exception?

Section 3 - Messages

This section defines the messages that can be exchanged between the client and the server.

    What are the messages/actions?
    What are the parameters?
    What are the return values?
    What are the exceptions?

Always try to describe these for a given context, not from each point of view (e.g. "making an order" with the input/outputs from the client to the server and the responses instead of "the client sends these messages and the server replies these messages with these outputs"). It makes it way easier to understand and to implement.
Section 4 - Examples

This section defines examples of messages that can be exchanged between the client and the server and the exchange order:

    What are the examples of messages/actions?
    What are the examples of exceptions?

It is important to define these examples to illustrate the protocol and to help the reader to understand the protocol using sequence or state diagrams.