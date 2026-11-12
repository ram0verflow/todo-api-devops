package com.abhirama.todo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void infoEndpointReturnsAppName() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app").value("todo-api"));
    }

    @Test
    void addTodoThenFindIt() throws Exception {
        mvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write Jenkinsfile\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Write Jenkinsfile"))
                .andExpect(jsonPath("$.done").value(false));

        mvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title == 'Write Jenkinsfile')]").exists());
    }

    @Test
    void blankTitleIsRejected() throws Exception {
        mvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
