package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.model.Task;
import com.hepsiemlak.todo.repository.TaskRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

/**
 * @author suleyman.yildirim
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Testcontainers
class TaskServiceTest {

    static private final String           couchbaseBucketName        = "todo-bucket";
    static private final String           username                   = "admin";
    static private final String           password                   = "password";
    private static final BucketDefinition bucketDefinition           = new BucketDefinition(couchbaseBucketName);

    private static final DockerImageName COUCHBASE_IMAGE_ENTERPRISE = DockerImageName.parse("couchbase:enterprise")
            .asCompatibleSubstituteFor("couchbase/server")
            .withTag("6.0.1");

    @Mock
    private TaskRepository taskRepository;

    @Container
    final static CouchbaseContainer couchbaseContainer = new CouchbaseContainer(COUCHBASE_IMAGE_ENTERPRISE)
            .withCredentials(username, password)
            .withBucket(bucketDefinition)
            .withStartupTimeout(Duration.ofSeconds(90))
            .waitingFor(Wait.forHealthcheck());

    @DynamicPropertySource
    static void registerCouchbaseProperties(DynamicPropertyRegistry registry) {
        //Start the Couchbase container and wait until it is running.
        couchbaseContainer.start();
        await().until(couchbaseContainer::isRunning);

        //Get the randomly created container ports to override default port numbers.
        int bootstrapHttpSslPort = couchbaseContainer.getMappedPort(18091);
        int bootstrapCarrierSslPort = couchbaseContainer.getMappedPort(11207);


        //Couchbase properties overriding based on couchbase container.
        registry.add("couchbase.cluster1", couchbaseContainer::getContainerIpAddress);
        registry.add("couchbase.bootstrapHttpDirectPort", couchbaseContainer::getBootstrapHttpDirectPort);
        registry.add("couchbase.bootstrapHttpSslPort", () -> bootstrapHttpSslPort);
        registry.add("couchbase.bootstrapCarrierDirectPort", couchbaseContainer::getBootstrapCarrierDirectPort);
        registry.add("couchbase.bootstrapCarrierSslPort", () -> bootstrapCarrierSslPort);
        registry.add("couchbase.bucket.usersession.name", couchbaseContainer::getUsername);
        registry.add("couchbase.bucket.usersession.password", couchbaseContainer::getPassword);
        registry.add("couchbase.bucket.configuration.name", couchbaseContainer::getUsername);
        registry.add("couchbase.bucket.configuration.password", couchbaseContainer::getPassword);
        registry.add("couchbase.bucket.bucketOpenTimeout", () -> 250000);
        registry.add("couchbase.bucket.operationTimeout", () -> 600000);
        registry.add("couchbase.bucket.observableTimeoutMilliSeconds", () -> 650000);
        registry.add("couchbase.bucket.ioPoolSize", () -> 3);
        registry.add("couchbase.bucket.computationPoolSize", () -> 3);
    }
    @InjectMocks
    private TaskService taskService;

    private Task task;
    @BeforeEach
    public void setUp() {
        task = Task.builder()
                .id(1L)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(1L)
                .build();

        couchbaseContainer.start();
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        couchbaseContainer.stop();
    }
    @Ignore(value = "to be done after finishing couchbase configuration")
    void testCreateTask_Success() {

        // Arrange
        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        var createdTask =  taskService.createTask(task);

        // Assert
        assertTrue(Objects.deepEquals(task, createdTask));

    }
}