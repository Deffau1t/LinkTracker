package backend.academy.scrapper;

import backend.academy.scrapper.controller.LinksController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LinksControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TgChatServices tgChatService;

    @InjectMocks
    private LinksController linksController;

    @Test
    public void testGetLinks() throws Exception {
        // Настройка MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(linksController).build();

        // Настройка моков
        when(tgChatService.isChatsRegistered(anyLong())).thenReturn(true);

        mockMvc.perform(get("/links")
            .header("Tg-Chat-Id", 123)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    }
}
