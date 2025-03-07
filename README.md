# Project installation

## Docker guide v1:

1. clone the project https://github.com/otipz19/ukma_db_project_kfc_backend

2. run mvn clean package -DskipTests

3. open terminal in the root folder

4. run docker build --tag "kfc" .

5. run docker run -p 8080:8080 -p 9990:9990 -e JAVA_OPTS="-XX:UseSVE=0" kfc

## WildFly guide v1:

1. download https://www.wildfly.org/downloads/  35.0.1.Final

2. unzip to chosen folder

3. go to bin/ subfolder

4. run ./add-user.sh (or bat for windows) and create admin account for management console

5. clone the project https://github.com/otipz19/ukma_db_project_kfc_backend

6. add Idea wildfly configuration
![add_idea_wildfly_config_1.png](readme%2Fimages%2Fadd_idea_wildfly_config_1.png)
![add_idea_wildfly_config_2.png](readme%2Fimages%2Fadd_idea_wildfly_config_2.png)

7. add Idea docker configuration
![add_idea_docker_config.png](readme%2Fimages%2Fadd_idea_docker_config.png)

8. add Idea compound configuration
![add_idea_compound_config.png](readme%2Fimages%2Fadd_idea_compound_config.png)

9. run compound configuration