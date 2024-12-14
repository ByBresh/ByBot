# ByBot
## A bot made for my discord server

ByBot is a discord bot that I made for my discord server, written in kotlin.

For now, it just stores some data in a database, but I plan on adding a few commands later on.

Docker Compose is used to run the bot and the database.
Sample docker-compose.yml file:

```
services:
  bybot:
    image: bybresh/bybot
    restart: always
    environment:
      BOT_TOKEN: YOUR_BOT_TOKEN
      DB_URL: mariadb:3306
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      - mariadb
    mariadb:
      image: mariadb
      restart: always
      environment:
        MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
        MYSQL_DATABASE: ${DB_NAME}
        MYSQL_USER: ${DB_USER}
        MYSQL_PASSWORD: ${DB_PASSWORD}
      volumes:
        - ./data:/var/lib/mysql
```

### Please make sure you give the bot the following permissions:
#### General Permissions:
- View Channels
#### Text Permissions:
- Send Messages
- Embed Links
- Attach Files
- Read Message History
- Add Reactions
