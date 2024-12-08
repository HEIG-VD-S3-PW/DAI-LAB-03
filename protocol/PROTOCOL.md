# Streaming Video Manager

## 1. Overview
Le protocole "Video Manager Application" (VMA) est un protocole permettant de récupérer ou déposer des fichiers vidéos sur
un serveur.


## 2. Transport protocol
VMA est un protocle client-serveur qui permet le listage, l'ajout, la suppression et le téléchargement de vidéos.  
VMA utilise le protocole TCP pour ces échanges. Par défaut, le port à utiliser est 1986.

Les échanges entre client et serveur sont les suivants :
- Envoie d'une commande et de ses arguments du client au serveur.
- Transfert de données sous forme de texte encodé en Base64. Ces échanges sont de client à serveur lors d'un upload et
  de serveur à client lors d'un download. Les délimiteurs sont respectivement ```END_OF_UPLOAD``` et ```END_OF_DOWNLOAD```.
- Envoie d'un code suivit d'un message du serveur au client. Le code peut représenter une confirmation ou une erreur.
  Le message décrit le code.


Pour toutes les commandes, un contrôle de validité est effectué côté serveur sur la commande et ses arguments. Si
l'exectution de la commande rencontre un problème, le serveur enverra une erreur au client. En cas d'erreur, le client
affichera le message.

Si il parvient à se connecter au serveur, le client doit se connecter au service avant de pouvoir envoyer d'autres
messages. C'est le clients qui initie la connexion. L'utilisation de la commande ```CONNECT``` permet de lancer une
authentification interactive gérée par le client. Celui-ci demande à l'utilisateur et vérifie son username et email. Il
construit ensuite la commande et l'envoie au serveur.  
Sinon, il est possbile d'envoyer directement ```CONNECT <Username> <Email>```, la gestion est alors uniquement effectuée
par le serveur.  
Le serveur retourne une confirmation.

L'utilisateur peut demander la liste des vidéos. Le serveur lui retourne alors une confirmation et la liste des vidéos
qui sont disponibles. En cas de problème, le serveur retourne une erreur.

L'utilisateur peut demander à télécharger une vidéo. Le serveur retourne une confirmation et commence à envoyer les
données. Lorsque le client reçoit la confirmation, il commence à receptionner les données. La fin de l'envoie est marquée
par son délimiteur.

L'utilisateur peut demander à ajouter une vidéo au server. L'utilisation de la commande UPLOAD permet de lancer un
interface interactif pour le parametrage de l'upload gérée par le client. Celui-ci demande à l'utilisateur et vérifie le
titre, la description et le chemin de la video. Il construit ensuite la commande et l'envoie au serveur.  
Le serveur retourne une confirmation pour que le transfert commence et se termine avec le délimiteur.


L'utilisateur peut demander à supprimer une vidéo. Le serveur contrôle que la vidéo existe et qu'elle estdisponible pour
être supprimée. Si c'est le cas, il la supprime. Sinon il retourne une erreur.

Lorsque le client a fini d'utiliser le service, il utilise la commande (QUIT) pour terminer la connexion. L'utilisateur 
est alors supprimé de la liste des utilisateurs et le serveur retourne une confirmation.

## 3. Messages
Dans tout les cas, le serveur va répondre avec un code et un message.

- ``` <CommandResponseCode> <Message>```

Liste des codes : 200 (OK), 500 (ERROR), 404 (NOT_FOUND), 403 (FORBIDDEN), 401 (UNAUTHORIZED).

Pour l'ensemble des commandes, sauf ```CONNECT``` et ```QUIT```, le serveur répondra toujours par le code ```401```
(UNAUTHORIZED) et par le message ```You have to be connected to execute this command``` si le client n'est pas connecté.

### Connexion au serveur

Le client demande au serveur pour se connecter.

#### Message

```CONNECT <pseudo> <email>```

#### Réponse

- Pour le code ```200``` (OK). La connexion est validée.
  ```Connection successfull```

- Pour le code ```500``` (ERROR)

    - Si le pseudo est invalide:
      ```Invalid pseudo```
    - Si l'adresse mail est invalide:
      ```Invalid email address```
    - Si l'utilisateur existe déjà:
      ```User already exists```
    - Si le nombre d'arguments n'est pas valide:
      ```Server error : The connect command expects exactly two arguments (CONNECT <pseudo> <email>)```

### Lister les vidéos

Le client demande au serveur la liste des vidéos.

#### Message

```LIST```

#### Réponse

- Pour le code ```200``` (OK), on reçoit la liste des vidéos disponibles.   
  ```1,<titre_1>,<description_1>;2,<titre_2>,<description_2>;...;n,<titre_n>,<description_n>;```

- Pour le code ```404``` (NOT_FOUND)
    - Pas de vidéos stockées sur le serveur:
      ```No videos found```

### Téléchargement d'une vidéo

Le client demande au serveur pour télécharger une vidéo. Cette commande se passe en deux temps. Premièrement, le serveur confirme que la vidéo est disponible en envoyant un code ```200``` (OK) et le titre de la vidéo. Ensuite, le serveur envoie les données de la vidéo sous forme de flux.

Le flux est considéré comme terminé lorsque le serveur envoie le délimiteur ```END_OF_DOWNLOAD```. Lorsque le client reçoit ce délimiteur, il arrête de lire les données, et le téléchargement est considéré comme terminé.

#### Message

```DOWNLOAD <videoChoice>```

#### Réponse

- Pour le code ```200``` (OK).
    - Confirmation du début du téléchargement: ```<titre_video>```

- Pour le code ```404``` (NOT_FOUND)
    - La video demandée n'existe pas: ```Invalid/unknown video choice```
- Pour le code ```403``` (FORBIDDEN)
    - La vidéo n'est actuellement pas disponible: ```Video is currently being deleted or is unavailable```
- Pour le code ```500``` (ERROR)
    - Si le nombre d'arguments n'est pas valide:
      ```Server error : The download command expects exactly one argument (DOWNLOAD <videoChoice>)```

#### Exception

En cas d'exception lors du transfert, le serveur retournera un code ```500``` (ERROR) suivit du message ```Error while 
downloading video : <Exception_message>```

### Dépôt d'une vidéo

Le client demande au serveur pour déposer une vidéo.

#### Message

```UPLOAD <Base64_titre> <Base64_description>```

#### Réponse

La réponse se fait en trois temps. Le serveur confirme ou non les informations reçue pour lancer le transfert. Après cette confirmation, le serveur se prépare à recevoir les données de la vidéo sous forme de flux envoyé par chunk et encodé en Base64.

Le flux est considéré comme terminé lorsque le client envoie le délimiteur ```END_OF_UPLOAD```. Le serveur confirme ensuite si le transfert c'est bien passé, et la vidéo est alors disponible.

- Validation pour débuter le transfert
    - Pour le code ```200``` (OK)
        - Confirmation du début du téléchargement: ```Ready to receive video```
    - Pour le code ```500``` (ERROR)
        - Problème dans l'encodage du titre ou de la descritpion: ```Invalid Base64 encoding```
        - Si le nombre d'arguments n'est pas valide:
          ```Server error : The upload command expects title and description```
    - Pour le coce ```401``` (UNAUTHORIZED)
        - L'utilisateur n'est pas connecté: ```You have to be connected to execute this command```

- Confirmation du transfert
    - Pour le code ```200``` (OK)
        - Confirmation du succès du téléchargement: ```Video uploaded successfully```

#### Exception

En cas d'exception lors du transfert, le serveur retournera un code ```500``` (ERROR) suivit du message ```Error while 
uploading video : <Exception_message>```

### Suppression d'une vidéo

Le client demande au serveur de supprimer une vidéo.

#### Message

```DELETE <videoChoice>```

#### Réponse

- Pour le code ```200``` (OK).
    - Confirmation de la suppression: ```Video deleted```
- Pour le code ```404``` (NOT_FOUND)
    - La video demandée n'existe pas: ```Video not found```
- Pour le code ```403``` (FORBIDDEN)
    - La vidéo n'est actuellement pas disponible: ```Video is currently being downloaded by other users```
- Pour le code ```500``` (ERROR)
    - Si le nombre d'arguments n'est pas valide:
      ```Server error : The delete command expects exactly one argument (DELETE <videoChoice>)```

#### Exception

En cas d'exception lors de la suppression, le serveur retournera un code ```500``` (ERROR) suivit du message ```Server 
error : <Exception_message>```  

### Déconnexion du serveur

Le client se décocnnecte du serveur.

#### Message

```QUIT```

#### Réponse

- Pour le code ```200``` (OK).
    - Confirmation de la déconnexion: ```See you soon :)```

#### Exception

Côté serveur aucune exception précise est gérée. Le serveur renvoie dans tous les cas un code ```200```, même si par exemple le client n'était pas connecté.


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
