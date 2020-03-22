package se.swb.fuel.modules.consumption.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import se.swb.fuel.modules.consumption.services.ConsumptionService;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ConsumptionControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ConsumptionService consumptionService;

    @Test
    public void shouldReturnExceptionWhenFileIsEmpty() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv",
                "text/plain", "".getBytes());
        this.mvc.perform(multipart("/api/consumption/upload", multipartFile)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldParseFileWithNoException() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        MockMultipartFile mpf = new MockMultipartFile(
                "file",
                "test.csv",
                "text/plain",
                classLoader.getResource("testData.csv")
                        .getContent().toString()
                        .getBytes()
        );

        this.mvc.perform(multipart("/api/consumption/upload").file(mpf)).andExpect(status().is2xxSuccessful());

        then(this.consumptionService).should().parseFile(mpf);
    }
}
