# todo-api-devops

A small Todo REST API (Java 17, Spring Boot, Maven) run through a full DevOps pipeline:
**Git → Maven → Jenkins (+ GitHub Actions) → Ansible → Docker → Kubernetes**.

DevOps self-learning activity (24CYAEC59), Abhirama M (1MS24CY002), Ramaiah Institute of Technology.

## API

| Method | Path                 | What it does                         |
|--------|----------------------|--------------------------------------|
| GET    | `/`                  | App info + which pod/host answered   |
| GET    | `/todos`             | List tasks                           |
| GET    | `/todos/{id}`        | One task (404 if missing)            |
| POST   | `/todos`             | Add a task: `{"title": "..."}`       |
| GET    | `/actuator/health`   | Health (used by Kubernetes probes)   |

## Component 1: CI

```bash
./mvnw clean package          # runs 4 JUnit tests, builds target/todo-api-1.0.0.jar
java -jar target/todo-api-1.0.0.jar
curl localhost:8080/todos
```

* `Jenkinsfile`: Checkout → Test (JUnit report) → Build → archive JAR. Triggered by `pollSCM` every minute.
* Enhancements: test stage before build, JAR versioned with the Jenkins build number
  (`todo-api-1.0.<BUILD_NUMBER>.jar`), and `.github/workflows/ci.yml` running the same build on GitHub Actions.

## Component 2: Ansible + Kubernetes

```bash
# Ansible: configure a Docker container acting as a server
docker build -t ansible-target ansible/target-host
docker run -d --name appserver -p 8081:8080 ansible-target
cd ansible && ansible-playbook -i inventory.ini playbook.yml

# Docker (multi-stage) + Kubernetes on minikube
minikube start
eval $(minikube docker-env)
docker build -t todo-api:1.0 .
kubectl apply -f k8s/
kubectl get pods -l app=todo-api
kubectl scale deployment todo-api --replicas=5
minikube service todo-api-service --url
```

Enhancements: multi-stage Dockerfile, ConfigMap for settings, liveness/readiness probes, rollback
(`kubectl set image deployment/todo-api todo-api=todo-api:broken` then `kubectl rollout undo deployment/todo-api`).
