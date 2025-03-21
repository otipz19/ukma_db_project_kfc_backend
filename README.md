# Project installation

## Docker guide:

1. clone the project <br>
https://github.com/otipz19/ukma_db_project_kfc_backend

2. login to github container registry<br>
docker login ghcr.io

3. go to docker/ subfolder<br>
cd docker/

4. run<br>
docker compose up --pull=always

## WildFly guide:

1. download WildFly 35.0.1.Final<br>
https://www.wildfly.org/downloads/

2. unzip to chosen folder

3. go to bin/ subfolder<br>
cd bin/

4. run ./add-user.sh (or bat for windows) and create admin account for management console

5. clone the project<br>
https://github.com/otipz19/ukma_db_project_kfc_backend

6. add Idea wildfly configuration
![add_idea_wildfly_config_1.png](readme%2Fimages%2Fadd_idea_wildfly_config_1.png)
![add_idea_wildfly_config_2.png](readme%2Fimages%2Fadd_idea_wildfly_config_2.png)

7. add Idea docker configuration
![add_idea_docker_config.png](readme%2Fimages%2Fadd_idea_docker_config.png)

8. add Idea compound configuration
![add_idea_compound_config.png](readme%2Fimages%2Fadd_idea_compound_config.png)

9. run compound configuration