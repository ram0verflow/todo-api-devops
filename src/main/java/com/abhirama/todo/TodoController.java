package com.abhirama.todo;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    // In-memory store: no database needed for this assignment
    private final List<Todo> todos = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    // Comes from application.properties locally, from the ConfigMap in Kubernetes
    @Value("${app.message}")
    private String message;

    @Value("${app.version}")
    private String version;

    // GET / : app info, including which pod answered (useful when scaled)
    @GetMapping("/")
    public Map<String, String> info() throws Exception {
        return Map.of(
                "app", "todo-api",
                "message", message,
                "version", version,
                "host", InetAddress.getLocalHost().getHostName());
    }

    // GET /todos : list all tasks
    @GetMapping("/todos")
    public List<Todo> list() {
        return todos;
    }

    // GET /todos/{id} : one task, or 404
    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> get(@PathVariable long id) {
        return todos.stream()
                .filter(t -> t.id() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /todos {"title": "..."} : add a task
    @PostMapping("/todos")
    public ResponseEntity<Todo> add(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Todo todo = new Todo(nextId.getAndIncrement(), title, false);
        todos.add(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }
}
