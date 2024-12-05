# Amar Streaming Platform

## 1. Overview  
Le protocole "Amar Streaming Platform" (ASP) est un protocole permettant de télécharger une vidéo depuis le serveur et 
de la streamer au client en utilisant son lecteur de media [VLC](https://www.videolan.org/vlc/index.fr.html).


## 2. Transport protocol 
ASP est un protocle client-serveur qui permet le visionnage, le listage, l'ajout et la suppression de vidéos.  
ASP utilise le protocole TCP pour ces échanges. Par défaut, le port à utiliser est 1986.  

C'est le clients qui initie la connexion. Si il parvient à se connecter au serveur, le client doit se connecter au service 
(CONNECT) avant de pouvoir envoyer d'autres messages. Un système de vérification des entrées est implémenté au niveau 
client et serveur. Si celles-ci ne sont pas valides, le client va demandé à nouveau l'information, jusqu'à validation. 
Du côté serveur, il retournera un code d'erreur avec un message. Si les informations sont valides, le serveur accepte la
connexion et attend les messages du client.
Un contrôle de validité est effectué pour chaque message envoyé au serveur.  
L'utilisateur peut demander la liste des vidéos (LIST). Le serveur lui retourne alors la liste des vidéos qui sont 
disponibles.  
L'utilisateur peut demander à voir une vidéo (WATCH id_video). Le serveur contrôle que la vidéo existe, l'encode en
Base64 et l'envoie au client. Le client la télécharge et la decode dans un fichier temporaire. Une fois le fichier
télécharger, le client lance l'application VLC et joue la vidéo. Une fois le visionnage terminé, le fichier temporaire
s'efface.  
L'utilisateur peut demander à ajouter une vidéo à la liste (UPLOAD).  
L'utilisateur peut demander à supprimer une vidéo (DELETE id_video). Le serveur contrôle que la vidéo existe et la 
supprime.  

Lorsqu'une commande a fini de s'exécuter (normalement ou avec une erreur), le serveur retourne en attente des messages 
client.  
En cas d'erreur, le client affichera un code d'erreur ainsi qu'un message permettant d'identifier le problème.  

Lorsque le client a fini d'utiliser le service, il utilise la commande (QUIT) pour terminer la connexion.

## 3. Messages

### Connexion au serveur  

Le client demande au serveur pour se connecter.

### Lister les vidéos

Le client demande au serveur la liste des vidéos.
  
#### Message

```text
LIST
```  

#### Réponse  

Dans tout les cas, le serveur va répondre avec un code et un message.

- ``` <CommandResponseCode> <Message>```

    - Pour le code ```OK```, on reçoit en message la liste des vidéos : 
  ```text
  <numero_1>,<titre_1>,<description_1>;<numero_2>,<titre_2>,<description_2>;...;<numero_n>,<titre_n>,<description_n>;
  ```
    - Message pour code ```ERROR```
      ```text
      No videos available in StreamingVideo
      ```



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