package event.club.admin.support;

import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ChairfrontMockContainer extends MockServerContainer {

    private static ChairfrontMockContainer mockServerContainer;

    private ChairfrontMockContainer() {
        super();
    }

    public static ChairfrontMockContainer getInstance() {
        if (mockServerContainer == null) {
            mockServerContainer = new ChairfrontMockContainer();
        }
        return mockServerContainer;
    }
    @Override
    public void start() {
        super.start();
        System.setProperty("CF_MOCK_LOCATION", mockServerContainer.getHost() +":" +mockServerContainer.getServerPort());
        new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort())
                .when(request()
                        .withPath("/register")
                )
                .respond(response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true"));
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
