## Local development using Docker

This is how you can easily use Docker to run OntoBrowser.

Required:\
Docker - https://docs.docker.com/get-docker/\

Run these commands from the project root where the docker-compose.yml file is located To start OntoBrowser and a mysql
database
```docker compose up```

To stop your containers ```docker compose down```

Running fully locally is a pain, but if you insist, you can check the Dockerfile to see what is required on your machine
to make it work, or try the install-oracle or install-mysql md files
