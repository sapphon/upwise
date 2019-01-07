package org.sapphon.personal.upwise.controller.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.controller.APIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddVoteControllerTest {

    @MockBean
    private APIController apiController;

    @MockBean
    private WisdomController wisdomController;

    @Autowired
    private MockMvc mvc;

    private String urlUnderTest;

    @Before
    public void setup() {
        urlUnderTest = "addvote";
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        mvc = MockMvcBuilders.standaloneSetup(new AddVoteController(apiController, wisdomController))
                .setViewResolvers(viewResolver)
                .build();

    }

    @Test
    public void testPostRequestWhereApiSaysBadRequestGets400Status() throws Exception {
        when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.BAD_REQUEST));
        MvcResult mvcResult = makeMockMvcPostWithBlankParams()
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(400), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestWhereApiSaysConflictGets409Status() throws Exception {
        when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CONFLICT));
        MvcResult mvcResult = makeMockMvcPostWithBlankParams()
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(409), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestWhereApiSaysCreatedGets201Status() throws Exception {
        when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
        MvcResult mvcResult = makeMockMvcPostWithBlankParams()
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestUsesLoggedInPrincipalsNameAsUsernameForVote() throws Exception {
        ArgumentCaptor<IVote> captor = ArgumentCaptor.forClass(IVote.class);
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("malvo");
        when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
        MvcResult mvcResult = makeMockMvcPostWithParamValuesAndPrincipal("morbo", "always fall up", principal)
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
            verify(apiController).voteForWisdomEndpoint(captor.capture());
            assertEquals("malvo", captor.getValue().getAddedByUsername());
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testDoesNotBlowUpIfSomehowThereIsNoLoggedInPrincipalAtTimeOfVoteSubmission() throws Exception {
        try {
            when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
            MvcResult mvcResult = makeMockMvcPostWithBlankParams()
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andReturn();
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Should tolerate the lack of an authenticated principal.");
        }
    }

    @Test
    public void testDefaultsToViewWisdom_IfNoDestinationViewIsSet() throws Exception {
        try {
            when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
            when(wisdomController.viewWisdom(any(), any(), any())).thenReturn("viewwisdom");
            MvcResult mvcResult = makeMockMvcPostWithBlankParams()
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andReturn();
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
            assertEquals("viewwisdom", mvcResult.getModelAndView().getViewName());
        } catch (Exception e) {
            Assert.fail("Should tolerate lack of destination view.");
        }
    }

    @Test
    public void testShowsLeaderboard_IfDestinationViewIsSetToLeaderboard() throws Exception {
        try {
            when(apiController.voteForWisdomEndpoint(any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
            when(wisdomController.getWisdomLeaderboardWithVotes(any(), any())).thenReturn("wisdomleaderboard");
            MvcResult mvcResult = makeMockMvcPostWithParamValues("jay", "jorb", "jim", "wisdomleaderboard")
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andReturn();
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addVoteStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
            assertEquals("wisdomleaderboard", mvcResult.getModelAndView().getViewName());
        } catch (Exception e) {
            Assert.fail("Destination view not set as expected.");
        }
    }


    private ResultActions makeMockMvcPostWithParamValues(String username, String content, String wiseMan, String redirectUrl) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/" + urlUnderTest).accept(MediaType.TEXT_HTML)
                .param("voterUsername", username)
                .param("wisdomContent", content)
                .param("wisdomAttribution", wiseMan)
                .param("destinationViewName", redirectUrl));
    }

    private ResultActions makeMockMvcPostWithParamValues(String username, String content, String wiseMan) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/" + urlUnderTest).accept(MediaType.TEXT_HTML)
                .param("voterUsername", username)
                .param("wisdomContent", content)
                .param("wisdomAttribution", wiseMan));
    }

    private ResultActions makeMockMvcPostWithParamValuesAndPrincipal(String content, String wiseMan, Principal principal) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/" + urlUnderTest).accept(MediaType.TEXT_HTML)
                .param("wisdomContent", content)
                .param("wisdomAttribution", wiseMan)
                .principal(principal));
    }

    private ResultActions makeMockMvcPostWithBlankParams() throws Exception {
        return this.makeMockMvcPostWithParamValues("", "", "");
    }

}